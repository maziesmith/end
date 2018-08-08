package yiome.projectend;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ListIterator;
import java.util.Random;

import yiome.projectend.entities.Ball;
import yiome.projectend.entities.DeadlyBall;
import yiome.projectend.entities.EndWall;
import yiome.projectend.entities.FillCircle;
import yiome.projectend.entities.NiceBall;
import yiome.projectend.entities.Particle;
import yiome.projectend.entities.Player;
import yiome.projectend.entities.Sketchable;
import yiome.projectend.entities.Spike;
import yiome.projectend.entities.Wall;
import yiome.projectend.gamemath.Collision;
import yiome.projectend.gamemath.Reflection;
import yiome.projectend.gamemath.Vector2f;

/**
 * GameThread is responsible for updating every object,
 * checking collisions and performing unique onCollision().
 * Game loop is contained in run() method, it calculates elapsed time,
 * performs currentTask_toDo(), and goes to sleep for few milliseconds.
 * All possible things that loop can do are Tasks, defined bellow.
 * **/

class GameThread implements Runnable {
    private boolean isRunning = true;
    boolean isActive = true;
    private boolean restart = false;

    private final float TARGET_TICK_RATE;
    private final float OPTIMAL_TIME;

    private CanvasView theView;
    private EntityManager entityMng;
    private SoundManager soundMng;
    private Context appContext;

    private Task currentTask;

    GameThread(CanvasView cView, EntityManager entityManager, SoundManager soundManager, Context context, final float refreshRate) {
        theView = cView;
        entityMng = entityManager;
        soundMng = soundManager;
        appContext = context;

        TARGET_TICK_RATE = refreshRate;
        OPTIMAL_TIME = 1000.f/TARGET_TICK_RATE;
    }

    public int getCurrentTaskID() { return currentTask.getID(); }

    public void forceRestart() { restart = true; }

    private void saveLevelToFile(String filename, int level) {
        FileOutputStream outputStream;
        try {
            outputStream = appContext.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(Integer.toString(level).getBytes());
            outputStream.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private int getLevelFromFile(String filename) {
        int startLevel = 0;
        boolean fileFound = false;

        String[] files = appContext.fileList();
        for (String s: files) {
            if (s.equals(filename)) {
                fileFound = true;
                break;
            }
        }

        if(fileFound) {
            FileInputStream in;
            try {
                in = appContext.openFileInput("lvl");
                byte[] fileContent = new byte[in.available()];
                in.read(fileContent);
                startLevel = Integer.parseInt(new String(fileContent));

            } catch (Exception e) {
                e.printStackTrace();
                saveLevelToFile(filename, 0);
            }

        } else {
            saveLevelToFile(filename, 0);
        }

        return startLevel;
    }

    public void run() {
        //reading and loading current level
        loadLevel(getLevelFromFile("lvl"));

        //setup of main loop

        int counter = 0;
        float second = 0f;

        long timeInMilliseconds = System.currentTimeMillis();
        float elapsedTime;

        while(isRunning) {

            while (isActive) {
                //this is some primitive thread sync, but for now it does the job
                while (entityMng.graphicMng.DIRTY_FLAG) {
                }
                entityMng.graphicMng.DIRTY_FLAG = true;

                //interpolation timer
                //elapsed time is in SECONDS
                elapsedTime = (System.currentTimeMillis() - timeInMilliseconds) / (float) 1000;
                //safety check for tick rates below 30 Hz, game will just slow down to prevent physic bugs
                if (elapsedTime > 0.03f) elapsedTime = 0.03f;
                timeInMilliseconds = System.currentTimeMillis();

                currentTask.toDo(elapsedTime);

                //check if tasks should be restarted
                if (restart) {
                    loadLevel(getCurrentTaskID());
                    restart = false;
                }

                entityMng.graphicMng.DIRTY_FLAG = false;
                //https://stackoverflow.com/questions/29219372/postinvalidateonanimation-vs-postinvalidate
                //let graphic thread do the work
                theView.postInvalidate(); // another option: postInvalidateOnAnimation()
                second += elapsedTime;
                counter++;
                if (second >= 1f) {
                    Log.i("FPS", Integer.toString(counter) + " " + Float.toString(entityMng.player.position.x) + " " + Float.toString(entityMng.player.position.y));
                    second = 0;
                    counter = 0;
                }

                //8 milliseconds of buffer at 60 Hz
                float buffer = 480f / TARGET_TICK_RATE;
                int sleepTime = (int) (OPTIMAL_TIME - (elapsedTime * 1000f) - buffer);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }


            //Second loop
            try { Thread.sleep(2000); } catch (InterruptedException e1) { e1.printStackTrace(); }
        }
        //when finishing
    }

    //check if player is ded or maybe he won
    void checkWinLose() {
        if(entityMng.player.haveWon) {
            //Winner, winner, chicken dinner!
            currentTask = new Winner(currentTask.getID() + 1);
        } else if(entityMng.player.alive == false) currentTask = new Loser(currentTask.getID());
    }

    private int amountOfDeadlyBalls() {
        int amount = 0;
        for(Ball b: entityMng.ballList) { if(b instanceof DeadlyBall) amount++; }
        return amount;
    }

    private int amountOfNeutrualBalls() {
        int amount = 0;
        for(Ball b: entityMng.ballList) { if(!(b instanceof DeadlyBall) && !(b instanceof NiceBall)) amount++; }
        return amount;
    }

    private int amountOfNiceBalls() {
        int amount = 0;
        for(Ball b: entityMng.ballList) { if(b instanceof NiceBall) amount++; }
        return amount;
    }

    private int amountOfSpikes() { return entityMng.spikeList.size(); }

    /*
     * This is the filling of the game loop used by pretty much every level
     * */
    void standardUpdate(float elapsedTime) {
        //look for destroyed Entities and remove them
        //iterator is used to prevent concurrent remove exception
        {
            Spike s;
            ListIterator<Spike> spikeIter = entityMng.spikeList.listIterator();
            while(spikeIter.hasNext()) {
                if((s = spikeIter.next()).alive == false) {
                    entityMng.newParticles(Particle.randomParticlesInCircle(s.vertices[0], 24f, 5f, 128f, Color.RED, 24, 0.8f));
                    soundMng.playPop(s.vertices[0]);
                    spikeIter.remove();
                    entityMng.remove(s);
                }
            }

            Ball b;
            ListIterator<Ball> ballIter = entityMng.ballList.listIterator();
            while(ballIter.hasNext()) {
                if((b = ballIter.next()).alive == false) {
                    soundMng.playPop(b.getPosition());
                    ballIter.remove();
                    entityMng.remove(b);
                }
            }

            Particle p;
            ListIterator<Particle> particleIter = entityMng.particleList.listIterator();
            while(particleIter.hasNext()) {
                if((p = particleIter.next()).alive == false) {
                    particleIter.remove();
                    entityMng.remove(p);
                }
            }
        }

        //ball collisions with each other
        {
            Ball b1, b2;
            for(int a = 0; a < entityMng.ballList.size(); a++) {
                b1 = entityMng.ballList.get(a);
                for(int b = a + 1; b < entityMng.ballList.size(); b++) {
                    b2 = entityMng.ballList.get(b);
                    if(Collision.check(b1, b2)) {
                        soundMng.playKlak(b1.getPosition());
                        Reflection.twoBalls(b1, b2);
                        b1.onCollision(b2);
                        b2.onCollision(b1);
                    }

                }
            }
        }

        //ball collisions with walls
        {
            Vector2f closestPoint;
            int particleAmount;
            float particleSpeed;
            for(Ball b: entityMng.ballList) {
                for(Wall w: entityMng.wallList) {
                    if((closestPoint = Collision.check(b, w)) != null) {
                        soundMng.playPipe(closestPoint);
                        particleSpeed = b.getVelocity().length()*0.2f;
                        particleAmount = (int)(particleSpeed*0.06f);
                        entityMng.newParticles(Particle.randomParticlesInCircle(closestPoint, 8f, 4f, particleSpeed, b.ballPaint.getColor(), particleAmount, 0.2f));
                        w.onCollide(b, closestPoint);
                    }
                }
            }
        }

        //player collisions with spikes
        //this one is EXTREMELY costly
        for(Spike s: entityMng.spikeList) {
            for(Ball b: entityMng.ballList) {
                if(b instanceof DeadlyBall) continue;
                if(Vector2f.subtract(s.getPosition(), b.getPosition()).length() > 500f) continue;
                if(Collision.check(b, s)) {
                    s.onCollision(b);
                    b.onCollision(s);
                }
            }
        }

        //update balls
        for(Ball b: entityMng.ballList) {
            b.update(elapsedTime);
        }
        //update camera
        entityMng.graphicMng.update(elapsedTime);
        //update particles
        for(Particle p: entityMng.particleList) p.update(elapsedTime);
        //check winning condition
        checkWinLose();
    }

    /*
    * Below are possible states (Tasks) of the game loop.
    * These classes have access to all fields in GameThread.
    * Those are: welcome screen concrete levels,
    * winner screen, loser screen.
    * */

    Task getCurrentTask() {
        return currentTask;
    }

    private abstract class Task {
        abstract int getID();
        abstract void toDo(float elapsedTime);
    }

    private class Winner extends Task {
        int nextID;
        float time = 0;
        final float END_TIME = 1.2f;
        FillCircle fillCircle;
        @Override
        int getID() { return nextID; }

        Winner(int next) {
            soundMng.playWin();
            nextID = next;
            fillCircle = new FillCircle(entityMng.player.getPosition(), Color.BLACK, 5f);
            entityMng.graphicMng.addSketchable(fillCircle);
        }

        @Override
        void toDo(float elapsedTime) {
            if(time > END_TIME) {
                loadLevel(nextID);
            } else {
                time += elapsedTime;
                fillCircle.update(elapsedTime);
            }
        }
    }

    private class Loser extends Task {
        int nextID;
        float time = 0f;
        final float START_ANIMATION_TIME = 0;

        @Override
        int getID() { return nextID; }

        Loser(int currentID) {
            soundMng.playLose();
            nextID = currentID;
            entityMng.player.desiredRadius = 0;
        }

        @Override
        void toDo(float elapsedTime) {
            time += elapsedTime;
            entityMng.graphicMng.update(elapsedTime);
            if(time > START_ANIMATION_TIME) {
                Player plr = entityMng.player;

                plr.updateRadius(elapsedTime);
                //add and animate some particles
                //entityMng.newParticles(Particle.randomParticlesInCircle(plr.getPosition(), plr.radius, 3f, 100, Color.BLACK, 2, 0.2f));

                for(Particle par: entityMng.particleList) par.update(elapsedTime);

                Particle p;
                ListIterator<Particle> particleIter = entityMng.particleList.listIterator();
                while(particleIter.hasNext()) {
                    if((p = particleIter.next()).alive == false) {
                        particleIter.remove();
                        entityMng.remove(p);
                    }
                }
                //check if animation is over
                if(plr.radius < 0f) {
                    loadLevel(nextID);
                }
            }
        }
    }

    private class TextScreen extends Task {
        int nextID;

        float time = 0;
        final float MOVE_TIME = 0.55f;
        final float LEAVE_TIME = 1.2f;
        private boolean countdownStarted = false;
        private StartTouchManager startTouchMng;

        int getID() { return nextID; }

        TextScreen(int afterThat, String text, int fontSize, int textColor, int backgroundColor) {
            nextID = afterThat;
            entityMng.buildTextScreen(text, fontSize, textColor, backgroundColor);
            startTouchMng = new StartTouchManager(entityMng.textScr, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            startTouchMng.manage();

            entityMng.textScr.update(elapsedTime);
            entityMng.graphicMng.update(elapsedTime);

            if(countdownStarted) {
                time += elapsedTime;
                if(time > MOVE_TIME) entityMng.graphicMng.setGoTo(new Vector2f(1650f, 0));
                if(time > LEAVE_TIME) {
                    loadLevel(nextID);
                }
            } else if(entityMng.textScr.noReturn) {
                countdownStarted = true;
                entityMng.graphicMng.setToFollow(null);
            }

        }

    }

    private class Level1 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 10; }

        Level1() {
            entityMng.buildLevel1();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level2 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 20; }

        Level2() {
            entityMng.buildLevel2();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level3 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 30; }

        Level3() {
            entityMng.buildLevel3();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level4 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 40; }

        Level4() {
            entityMng.buildLevel4();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level5 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 50; }

        Level5() {
            entityMng.buildLevel5();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            //custom win condition (if no DeadlyBalls left)
            if(amountOfDeadlyBalls() == 0) entityMng.player.haveWon = true;
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level6 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 60; }

        Level6() {
            entityMng.buildLevel6();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level7 extends Task {
        private final int countdownTime = 10;
        private PlayerTouchManager pTouchMng;
        int timeInSeconds = 0;
        float time = 0f;

        int getID() { return 70; }

        Level7() {
            entityMng.buildLevel7();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            time += elapsedTime;
            if(time >= 1f) {
                timeInSeconds += 1;
                time = 0f;
                if(timeInSeconds > countdownTime) {
                    entityMng.background.strings[0] = "well done!";
                } else {
                    entityMng.background.strings[0] = "survive " + (countdownTime - timeInSeconds) + " seconds";
                }
                if(timeInSeconds > countdownTime) entityMng.player.haveWon = true;
            }

            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level8 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 80; }

        Level8() {
            entityMng.buildLevel8();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level9 extends Task {
        private final int countdownTime = 15;
        private PlayerTouchManager pTouchMng;
        int timeInSeconds = 0;
        float time = 0f;

        int getID() { return 90; }

        Level9() {
            entityMng.buildLevel9();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            time += elapsedTime;
            if(time >= 1f) {
                timeInSeconds += 1;
                time = 0f;
                if(timeInSeconds > countdownTime) {
                    entityMng.background.strings[0] = "well done!";
                } else {
                    entityMng.background.strings[0] = "survive " + (countdownTime - timeInSeconds) + " seconds";
                }
                if(timeInSeconds > countdownTime) entityMng.player.haveWon = true;
            }

            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    private class Level10 extends Task {
        private PlayerTouchManager pTouchMng;

        int getID() { return 100; }

        Level10() {
            entityMng.buildLevel10();
            pTouchMng = new PlayerTouchManager(entityMng.player, theView.touchEventsQueue, entityMng.graphicMng);
        }

        public void toDo(float elapsedTime) {
            pTouchMng.manage();
            standardUpdate(elapsedTime);
        }

    }

    //clears all, builds and sets currentTask with new Task
    public void loadLevel(int ID) {
        entityMng.graphicMng.setDefaults();
        entityMng.freshStart();
        switch (ID) {
            case 0:
                /*soundMng.playStart();*/
                currentTask = new TextScreen(1, "nice to meet you!", 120, Color.BLACK, Color.WHITE);
                break;
            case 1:
                saveLevelToFile("lvl", 1);
                currentTask = new TextScreen(10, "level 1", 180, Color.WHITE, Color.BLACK);
                break;
            case 10:
                currentTask = new Level1();
                break;
            case 11:
                saveLevelToFile("lvl", 11);
                currentTask = new TextScreen(20, "level 2", 180, Color.WHITE, Color.BLACK);
                break;
            case 20:
                currentTask = new Level2();
                break;
            case 21:
                saveLevelToFile("lvl", 21);
                currentTask = new TextScreen(30, "level 3", 180, Color.WHITE, Color.BLACK);
                break;
            case 30:
                currentTask = new Level3();
                break;
            case 31:
                saveLevelToFile("lvl", 31);
                currentTask = new TextScreen(40, "level 4", 180, Color.WHITE, Color.BLACK);
                break;
            case 40:
                currentTask = new Level4();
                break;
            case 41:
                saveLevelToFile("lvl", 41);
                currentTask = new TextScreen(50, "level 5", 180, Color.WHITE, Color.BLACK);
                break;
            case 50:
                currentTask = new Level5();
                break;
            case 51:
                saveLevelToFile("lvl", 51);
                currentTask = new TextScreen(60, "level 6", 180, Color.WHITE, Color.BLACK);
                break;
            case 60:
                currentTask = new Level6();
                break;
            case 61:
                saveLevelToFile("lvl", 61);
                currentTask = new TextScreen(70, "level 7", 180, Color.WHITE, Color.BLACK);
                break;
            case 70:
                currentTask = new Level7();
                break;
            case 71:
                saveLevelToFile("lvl", 71);
                currentTask = new TextScreen(80, "level 8", 180, Color.WHITE, Color.BLACK);
                break;
            case 80:
                currentTask = new Level8();
                break;
            case 81:
                saveLevelToFile("lvl", 81);
                currentTask = new TextScreen(90, "level 9", 180, Color.WHITE, Color.BLACK);
                break;
            case 90:
                currentTask = new Level9();
                break;
            case 91:
                saveLevelToFile("lvl", 91);
                currentTask = new TextScreen(100, "level 10", 180, Color.WHITE, Color.BLACK);
                break;
            case 100:
                currentTask = new Level10();
                break;
            case 101:
                saveLevelToFile("lvl", 101);
                currentTask = new TextScreen(110, "more coming soon, thanks for playing!", 56, Color.WHITE, Color.BLACK);
                break;

            case -999:
                currentTask = new TextScreen(-1000, "if you tap again, game will reset", 60, Color.WHITE, Color.BLACK);
                break;

            case -1000:
                saveLevelToFile("lvl", 0);
                currentTask = new TextScreen(0, "all data removed!", 80, Color.BLACK, Color.WHITE);
                break;

            default:
                currentTask = new TextScreen(-999, "you got to the end!", 120, Color.BLACK, Color.WHITE);
                break;
        }
        entityMng.pushToGraphicQueue();
    }

}

//TODO more complex stuff like Graviton and Antigraviton