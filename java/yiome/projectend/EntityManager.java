package yiome.projectend;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yiome.projectend.entities.Background;
import yiome.projectend.entities.Ball;
import yiome.projectend.entities.DeadlyBall;
import yiome.projectend.entities.EndWall;
import yiome.projectend.entities.NiceBall;
import yiome.projectend.entities.Particle;
import yiome.projectend.entities.Sketchable;
import yiome.projectend.entities.Spike;
import yiome.projectend.entities.TextScreen;
import yiome.projectend.entities.Player;
import yiome.projectend.entities.Wall;
import yiome.projectend.gamemath.Vector2f;

/**
 * This class is responsible for access to all notable game objects.
 * Recipe for making every level (what entities it contains of, in which places) is contained there as a function.
 * Every entity should be created there, after that loaded to graphic queue in GraphicManager.
 * Build level functions are invoked from GameThread.
 **/

class EntityManager {
    GraphicManager graphicMng;

    public TextScreen textScr;
    public Background background;
    public Player player;
    public final List <Wall> wallList = new ArrayList<>();
    public final List <Ball> ballList = new ArrayList<>();
    public final List <Spike> spikeList = new ArrayList<>();
    public final List <Particle> particleList = new ArrayList<>();

    void freshStart() {
        player = new Player(new Ball.Builder());
        background = (new Background.Builder()).build();
        wallList.clear();
        ballList.clear();
        spikeList.clear();
        graphicMng.emptySketchables();
        graphicMng.placeCamera(new Vector2f(2000, 0));
        graphicMng.changeSpeed(6f);
    }

    void remove(Sketchable toRemove){
        graphicMng.removeSketchable(toRemove);
    }

    void newParticles(Particle[] particleArray) {
        for(Particle p: particleArray) {
            particleList.add(p);
            graphicMng.addSketchable(p);
        }
    }

    void pushToGraphicQueue() {
        graphicMng.addAllSketchables(wallList);
        graphicMng.addAllSketchables(ballList);
        graphicMng.addAllSketchables(spikeList);
    }

    void addTo(List l, Object[] ObjectsArr) {
        l.addAll(Arrays.asList(ObjectsArr));
    }

    EntityManager(GraphicManager mng) {
        graphicMng = mng;
    }

    /*
    * Below it is defined how to build all of the levels.
    * */

    void buildTextScreen(String text, int fontSize, int textColor, int backgroundColor) {
        textScr = new TextScreen(text, fontSize, textColor, backgroundColor);
        graphicMng.addSketchable(textScr);
        graphicMng.setToFollow(textScr);
        graphicMng.placeCamera(new Vector2f(1100, 0));
        graphicMng.changeSpeed(3f);
    }

    void buildLevel1() {
        Wall w;
        Spike s;
        Ball b;
        String[] strArr = new String[] {
                "hello!",
                "drag and drop",
                "to increase speed",
                "don't touch the red",
                "you are close"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-100f, 180),
                new Vector2f(-460f, 420f),
                new Vector2f(-460f, 500f),
                new Vector2f(-100f, -965f),
                new Vector2f(0f, -2300f)};
        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();

        graphicMng.addSketchable(background);
        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-200, 300), new Vector2f(200, 300)));
        wallList.add(new Wall(new Vector2f(-200, 300), new Vector2f(-200, -4500)));
        wallList.add(new Wall(new Vector2f(200, 300), new Vector2f(200, -4500)));
        addTo(spikeList, Spike.spikyWall(wallList.get(1), 1500f, 2500f, Spike.TO_FIT, 190f, false));
        addTo(spikeList, Spike.spikyWall(wallList.get(2), 2550f, 3500f, Spike.TO_FIT, 190f, true));
        addTo(spikeList, Spike.spikyWall(wallList.get(1), 3530f, 4500f, Spike.TO_FIT, 190f, false));
        wallList.add(new Wall(new Vector2f(-200, -4500), new Vector2f(300, -5300)));
        wallList.add(new Wall(new Vector2f(200, -4500), new Vector2f(900, -5600)));
        addTo(spikeList, Spike.spikyWall(wallList.get(4), 50f, 1200f, Spike.TO_FIT, 120f, true));
        wallList.add(new Wall(new Vector2f(900, -5600), new Vector2f(900, -10000)));
        wallList.add(new Wall(new Vector2f(300, -5300), new Vector2f(300, -10000)));
        addTo(spikeList, Spike.spikyWall(wallList.get(6), 100f, 1200f, Spike.TO_FIT, 120f, false));
        wallList.add(new Wall(new Vector2f(600, -6700), new Vector2f(450, -8500)));
        wallList.add(new Wall(new Vector2f(600, -6700), new Vector2f(750, -8500)));
        wallList.add(new Wall(new Vector2f(450, -8500), new Vector2f(750, -8500)));
        addTo(spikeList, Spike.spikyWall(wallList.get(7), 50, 1200, 80f, 100, true));
        addTo(spikeList, Spike.spikyWall(wallList.get(8), 1200, 1800, 80f, 100, false));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(5), 3600f, 220f, true)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(5), 3450f, 120f, true)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 3900f, 220f, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 3750f, 120f, false)));
        wallList.add(new EndWall(new Vector2f(900, -10000), new Vector2f(300, -10000)));
    }

    void buildLevel2() {
        Wall w;
        Ball.Builder bBuilder = (new Ball.Builder()).radius(48f).friction(0f);
        String[] strArr = new String[] {
                "this one is easy",
                "hello again!"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-270f, -900f),
                new Vector2f(-200f, 400f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-300, 300), new Vector2f(300, 300)));
        wallList.add(new Wall(new Vector2f(-300, 300), new Vector2f(-300, -2400)));
        wallList.add(new Wall(new Vector2f(300, 300), new Vector2f(300, -1800)));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(200, -700)).velocity(new Vector2f(450, 0))));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(2), 1700, 250, true)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(200, -1550)).velocity(new Vector2f(450, 0))));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(1), 2000, 250, false)));

        wallList.add(new Wall(new Vector2f(-300, -2400), new Vector2f(1800, -2400)));
        wallList.add(new Wall(new Vector2f(300, -1800), new Vector2f(1200, -1800)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(3), 900, 245, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(4), 300, 245, true)));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(750, -2330)).velocity(new Vector2f(0, 450))));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(3), 1200, 245, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(4), 600, 245, true)));

        wallList.add(new Wall(new Vector2f(1800, -2400), new Vector2f(1800, -300)));
        wallList.add(new Wall(new Vector2f(1200, -1800), new Vector2f(1200, -300)));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(5), 1200, 245, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 600, 245, true)));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1750, -1350)).velocity(new Vector2f(-450, 0))));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(5), 900, 245, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 300, 245, true)));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1700, -1050)).velocity(new Vector2f(450, 0))));

        wallList.add(new Wall(new Vector2f(1200, -2270), new Vector2f(1720, -2060)));
        wallList.add(new EndWall(new Vector2f(1800, -300), new Vector2f(1200, -300)));
    }

    void buildLevel3() {
        Wall w;
        String[] strArr = new String[] {
                "remember, red is bad",
                "right"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-370f, -400f),
                new Vector2f(-200f, -1700f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);
        Ball.Builder bBuilder = new Ball.Builder().radius(32f).friction(0f);

        wallList.add(new Wall(new Vector2f(-400, 500), new Vector2f(400, 500)));
        wallList.add(new Wall(new Vector2f(-400, 500), new Vector2f(-400, -4500)));
        wallList.add(new Wall(new Vector2f(400, 500), new Vector2f(400, -4500)));

        bBuilder.position(new Vector2f(-350, -1400)).velocity(new Vector2f(350f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -1400)).velocity(new Vector2f(-350f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-350, -1700)).velocity(new Vector2f(450f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -1700)).velocity(new Vector2f(-450f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-350, -2000)).velocity(new Vector2f(550f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -2000)).velocity(new Vector2f(-550f, 0f));
        ballList.add(new DeadlyBall(bBuilder));

        bBuilder.position(new Vector2f(-350, -3000)).velocity(new Vector2f(450f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -3000)).velocity(new Vector2f(-450f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-350, -3200)).velocity(new Vector2f(350f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -3200)).velocity(new Vector2f(-350f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-350, -3400)).velocity(new Vector2f(450f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -3400)).velocity(new Vector2f(-450f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-350, -3600)).velocity(new Vector2f(550f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -3600)).velocity(new Vector2f(-550f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-350, -3800)).velocity(new Vector2f(350f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(350, -3800)).velocity(new Vector2f(-350f, 0f));
        ballList.add(new DeadlyBall(bBuilder));

        wallList.add(new Wall(new Vector2f(-400, -4500), new Vector2f(-1400, -5500)));
        wallList.add(new Wall(new Vector2f(-1400, -5500), new Vector2f(-1400, -5800)));
        wallList.add(new Wall(new Vector2f(400, -4500), new Vector2f(1400, -5500)));
        wallList.add(new Wall(new Vector2f(1400, -5500), new Vector2f(1400, -5800)));
        wallList.add(new Wall(new Vector2f(0, -4800), new Vector2f(-1000, -5800)));
        wallList.add(new Wall(new Vector2f(0, -4800), new Vector2f(1000, -5800)));

        wallList.add(new Wall(new Vector2f(-1400, -5800), new Vector2f(-1400, -7000)));
        wallList.add(new Wall(new Vector2f(-1000, -5800), new Vector2f(-1000, -7000)));
        bBuilder.position(new Vector2f(-1200, -6000)).velocity(new Vector2f(-750f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-1200, -6400)).velocity(new Vector2f(800f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-1200, -6800)).velocity(new Vector2f(-850f, 0f));
        ballList.add(new DeadlyBall(bBuilder));
        wallList.add(new EndWall(new Vector2f(-1000, -7000), new Vector2f(-1400, -7000)));


        w = new Wall(new Vector2f(1400, -5800), new Vector2f(1400, -7000));
        spikeList.add(new Spike(Spike.stickOnWall(w, 400, 150, true)));
        spikeList.add(new Spike(Spike.stickOnWall(w, 600, 150, true)));
        spikeList.add(new Spike(Spike.stickOnWall(w, 800, 150, true)));
        wallList.add(w);

        w = new Wall(new Vector2f(1000, -5800), new Vector2f(1000, -7000));
        spikeList.add(new Spike(Spike.stickOnWall(w, 400, 150, false)));
        spikeList.add(new Spike(Spike.stickOnWall(w, 600, 150, false)));
        spikeList.add(new Spike(Spike.stickOnWall(w, 800, 150, false)));
        wallList.add(w);

        wallList.add(new EndWall(new Vector2f(1400, -7000), new Vector2f(1000, -7000)));
    }

    void buildLevel4() {
        Wall w;
        String[] strArr = new String[] {
                "gray is not red",
                "don't be rude"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-100f, -100),
                new Vector2f(-460f, 1000f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        Ball.Builder bBuilder = new Ball.Builder().radius(68f).friction(300f);

        wallList.add(new Wall(new Vector2f(-500, -500), new Vector2f(500, -500)));
        wallList.add(new Wall(new Vector2f(500, -500), new Vector2f(500, 2500)));
        wallList.add(new Wall(new Vector2f(-500, -500), new Vector2f(300, 800)));
        bBuilder.position(new Vector2f(385, 785));
        ballList.add(bBuilder.build());
        wallList.add(new Wall(new Vector2f(300, 800), new Vector2f(-500, 1600)));
        wallList.add(new Wall(new Vector2f(-500, 1600), new Vector2f(-500, 2500)));
        wallList.add(new Wall(new Vector2f(500, 2500), new Vector2f(0, 2500)));
        bBuilder.position(new Vector2f(150, 2095)).radius(45f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-245, 1785)).radius(90f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-90, 2400)).radius(70f);
        ballList.add(bBuilder.build());

        w = new Wall(new Vector2f(0, 2500), new Vector2f(0, 5300));
        addTo(spikeList, Spike.spikyWall(w, 200, 1400, Spike.TO_FIT, 360, false));
        wallList.add(w);
        bBuilder.position(new Vector2f(-445, 2700)).radius(20f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-380, 2850)).radius(75f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-410, 3390)).radius(60f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-260, 3800)).radius(56f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-65, 4310)).radius(36f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-155, 4450)).radius(86f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-235, 4990)).radius(36f);
        ballList.add(new DeadlyBall(bBuilder));

        w = new Wall(new Vector2f(-500, 2500), new Vector2f(-500, 5300));
        addTo(spikeList, Spike.spikyWall(w, 1500, 2400, Spike.TO_FIT, 360, true));
        wallList.add(w);
        wallList.add(new EndWall(new Vector2f(-500, 5300), new Vector2f(0, 5300)));

    }

    void buildLevel5() {
        Wall w;
        String[] strArr = new String[] {
                "eliminate red balls",
                "back button restarts the level"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-500f, -200f),
                new Vector2f(-500f, 280f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-800f, -900f), new Vector2f(800f, -900f))); //main
        wallList.add(new Wall(new Vector2f(900f, -800f), new Vector2f(900f, 800f))); //main

        wallList.add(new Wall(new Vector2f(800f, -900f), new Vector2f(900f, -800f)));

        wallList.add(new Wall(new Vector2f(800f, 900f), new Vector2f(-800f, 900f))); //main

        wallList.add(new Wall(new Vector2f(900f, 800f), new Vector2f(800f, 900f)));

        wallList.add(new Wall(new Vector2f(-900f, 800f), new Vector2f(-900f, -800f))); //main

        wallList.add(new Wall(new Vector2f(-800f, 900f), new Vector2f(-900f, 800f)));
        wallList.add(new Wall(new Vector2f(-900f, -800f), new Vector2f(-800f, -900f)));

        Ball.Builder bBuilder = new Ball.Builder().friction(10f);
        bBuilder.position(new Vector2f(700, 400)).radius(60f);
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-520, -220)).radius(45f);
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-720, 720)).radius(35f);
        ballList.add(new DeadlyBall(bBuilder));

        bBuilder.friction(100f);

        bBuilder.position(new Vector2f(-320, -420)).radius(35f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-180, 720)).radius(65f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-770, -10)).radius(25f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-675, 520)).radius(45f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(420, -430)).radius(85f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(300, 300)).radius(55f);
        ballList.add(bBuilder.build());
    }

    void buildLevel6() {
        Wall w;
        Ball.Builder bBuilder = new Ball.Builder();
        String[] strArr = new String[] {
                "a big one",
                "good luck"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-500f, -900f),
                new Vector2f(-500f, 400f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-400, 400), new Vector2f(400, 400)));
        wallList.add(new Wall(new Vector2f(-400, 400), new Vector2f(-400, -2140)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(1), 1200, 400, false)));
        wallList.add(new Wall(new Vector2f(400, 400), new Vector2f(400, -3100)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(2), 1350, 400, true)));

        wallList.add(new Wall(new Vector2f(-1200, -1500), new Vector2f(-80, -1500)));
        wallList.add(new Wall(new Vector2f(60, -1500), new Vector2f(400, -1500)));

        wallList.add(new Wall(new Vector2f(400, -2300), new Vector2f(-1060, -2300)));

        wallList.add(new Wall(new Vector2f(-1200, -1500), new Vector2f(-1200, -3100)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 750, 200, false)));

        wallList.add(new Wall(new Vector2f(-1200, -3100), new Vector2f(200, -3100)));
        wallList.add(new EndWall(new Vector2f(200, -3100), new Vector2f(400, -3100)));

        bBuilder.position(new Vector2f(10, -2730)).radius(370f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-650, -2700)).radius(50f);
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-750, -2850)).radius(30f);
        ballList.add(bBuilder.build());
        bBuilder.position(new Vector2f(-700, -2550)).radius(30f);
        ballList.add(bBuilder.build());

        bBuilder.position(new Vector2f(190, -2040)).radius(100f).velocity(new Vector2f(200f, 620f)).friction(0f);
        ballList.add(new DeadlyBall(bBuilder));
        bBuilder.position(new Vector2f(-250, -1750)).velocity(new Vector2f(500f, 320f));
        ballList.add(new DeadlyBall(bBuilder));

        bBuilder.position(new Vector2f(-800, -1820)).velocity(new Vector2f(0, 0)).friction(200f).radius(75f);
        ballList.add(new NiceBall(bBuilder));

        bBuilder.position(new Vector2f(-1050, -2040)).radius(50f);
        ballList.add(new DeadlyBall(bBuilder));

    }

    void buildLevel7() {
        Wall w;
        Ball.Builder bBuilder = new Ball.Builder();
        String[] strArr = new String[] {
                "survive 10 seconds"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-500, -200f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-800f, -900f), new Vector2f(800f, -900f))); //main
        wallList.add(new Wall(new Vector2f(900f, -800f), new Vector2f(900f, 800f))); //main

        wallList.add(new Wall(new Vector2f(800f, -900f), new Vector2f(900f, -800f)));

        wallList.add(new Wall(new Vector2f(800f, 900f), new Vector2f(-800f, 900f))); //main

        wallList.add(new Wall(new Vector2f(900f, 800f), new Vector2f(800f, 900f)));

        wallList.add(new Wall(new Vector2f(-900f, 800f), new Vector2f(-900f, -800f))); //main

        wallList.add(new Wall(new Vector2f(-800f, 900f), new Vector2f(-900f, 800f)));
        wallList.add(new Wall(new Vector2f(-900f, -800f), new Vector2f(-800f, -900f)));

        bBuilder.friction(0f);
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(300f, -260f)).velocity(new Vector2f(700f, 100f)).radius(55f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-660, 390f)).velocity(new Vector2f(400f, 500f)).radius(48f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-260, 200f)).velocity(new Vector2f(100f, 900f)).radius(70f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-160, 450f)).velocity(new Vector2f(700f, 800f)).radius(60f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-550, 420f)).velocity(new Vector2f(900f, 100f)).radius(48f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(650, 320f)).velocity(new Vector2f(550f, 200f)).radius(38f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(350, 720f)).velocity(new Vector2f(600f, 500f)).radius(58f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(850, -320f)).velocity(new Vector2f(700f, 800f)).radius(40f)));

    }

    void buildLevel8() {
        Wall w;
        Ball.Builder bBuilder = new Ball.Builder();
        String[] strArr = new String[] {
                "companion ball",
                "back restarts"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-200, -600f),
                new Vector2f(200, 900f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-200, -300), new Vector2f(200, -300)));
        wallList.add(new Wall(new Vector2f(-200, -300), new Vector2f(-200, 2400)));
        wallList.add(new Wall(new Vector2f(200, -300), new Vector2f(200, 2100)));

        wallList.add(new Wall(new Vector2f(-200, 2400), new Vector2f(1000, 2400)));
        wallList.add(new Wall(new Vector2f(200, 2100), new Vector2f(1300, 2100)));

        wallList.add(new Wall(new Vector2f(1000, 2400), new Vector2f(1000, 3200)));
        wallList.add(new Wall(new Vector2f(1300, 2100), new Vector2f(1300, 3200)));

        wallList.add(new EndWall(new Vector2f(1300, 3200), new Vector2f(1000, 3200)));

        ballList.add(new NiceBall(bBuilder.position(new Vector2f(-110, -170)).radius(50f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-110, 1050)).radius(80f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(90, 1670)).radius(60f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-150, 2050)).radius(70f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(480, 2170)).radius(55f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(860, 2320)).radius(50f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1225, 2740)).radius(42f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1050, 2780)).radius(42f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1170, 2200)).radius(52f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-70, 2250)).radius(58f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-100, 710)).radius(58f)));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(2), 900, 390, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(2), 1200, 390, false)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(2), 1500, 390, false)));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(3), 800, 290, true)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(4), 120, 290, false)));

        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(5), 200, 290, true)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 800, 290, false)));

    }

    void buildLevel9() {
        Wall w;
        Ball.Builder bBuilder = new Ball.Builder();
        String[] strArr = new String[] {
                "survive 15 seconds"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-500, -200f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-800f, -900f), new Vector2f(800f, -900f))); //main
        wallList.add(new Wall(new Vector2f(900f, -800f), new Vector2f(900f, 800f))); //main

        wallList.add(new Wall(new Vector2f(800f, -900f), new Vector2f(900f, -800f)));

        wallList.add(new Wall(new Vector2f(800f, 900f), new Vector2f(-800f, 900f))); //main

        wallList.add(new Wall(new Vector2f(900f, 800f), new Vector2f(800f, 900f)));

        wallList.add(new Wall(new Vector2f(-900f, 800f), new Vector2f(-900f, -800f))); //main

        wallList.add(new Wall(new Vector2f(-800f, 900f), new Vector2f(-900f, 800f)));
        wallList.add(new Wall(new Vector2f(-900f, -800f), new Vector2f(-800f, -900f)));

        bBuilder.friction(0f);
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(300f, -260f)).velocity(new Vector2f(700f, 100f)).radius(55f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-660, 390f)).velocity(new Vector2f(400f, 500f)).radius(48f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-260, 200f)).velocity(new Vector2f(100f, 900f)).radius(70f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-160, 450f)).velocity(new Vector2f(700f, 800f)).radius(60f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(-550, 420f)).velocity(new Vector2f(900f, 100f)).radius(48f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(650, 320f)).velocity(new Vector2f(550f, 200f)).radius(38f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(350, 720f)).velocity(new Vector2f(600f, 500f)).radius(58f)));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(850, -320f)).velocity(new Vector2f(700f, 800f)).radius(40f)));

    }

    void buildLevel10() {
        Wall w;
        Ball.Builder bBuilder = new Ball.Builder();
        String[] strArr = new String[] {
                "mess time",
                "push it"};
        Vector2f[] posArr = new Vector2f[] {
                new Vector2f(-300, -600f),
                new Vector2f(700, 600f)};

        background = (new Background.Builder()).slidingText(strArr, posArr, 0.7f, graphicMng).build();
        graphicMng.addSketchable(background);

        ballList.add(player);
        graphicMng.setToFollow(player);

        wallList.add(new Wall(new Vector2f(-300, -300), new Vector2f(300, -300)));
        wallList.add(new Wall(new Vector2f(-300, -300), new Vector2f(-300, 600)));
        wallList.add(new Wall(new Vector2f(300, -300), new Vector2f(300, 300)));

        wallList.add(new Wall(new Vector2f(-300, 600), new Vector2f(1200, 2100)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(3), 1680, 290, true)));
        wallList.add(new Wall(new Vector2f(300, 300), new Vector2f(1500, 1500)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(4), 1570, 290, false)));

        wallList.add(new Wall(new Vector2f(1500, 1500), new Vector2f(1500, 300)));

        wallList.add(new Wall(new Vector2f(1200, 2100), new Vector2f(2400, 2100)));
        spikeList.add(new Spike(Spike.stickOnWall(wallList.get(6), 180, 440, true)));

        wallList.add(new Wall(new Vector2f(2400, 2100), new Vector2f(2400, -300)));

        wallList.add(new Wall(new Vector2f(1500, 300), new Vector2f(2100, 300)));

        wallList.add(new Wall(new Vector2f(2100, 300), new Vector2f(2100, -300)));

        wallList.add(new EndWall(new Vector2f(2100, -300), new Vector2f(2400, -300)));

        ballList.add(new DeadlyBall(bBuilder.friction(0).position(new Vector2f(150, 1000)).velocity(new Vector2f(500, -500))));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(300, 1150)).velocity(new Vector2f(600, -600))));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(450, 1300)).velocity(new Vector2f(400, -400))));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(600, 1450)).velocity(new Vector2f(350, -350))));

        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1900, 560)).velocity(new Vector2f(1000, 0))));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(2260, 800)).velocity(new Vector2f(-850, 0))));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(2260, 700)).velocity(new Vector2f(-1050, 0))));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(1560, 1100)).velocity(new Vector2f(850, 0))));
        ballList.add(new DeadlyBall(bBuilder.position(new Vector2f(2320, 1270)).velocity(new Vector2f(950, 0))));
        ballList.add(new DeadlyBall(bBuilder.radius(80f).position(new Vector2f(2300, 170)).velocity(new Vector2f(450, 0))));

        ballList.add(bBuilder.radius(81f).friction(200f).position(new Vector2f(1900, 1500)).velocity(new Vector2f()).build());
        ballList.add(bBuilder.radius(41f).friction(200f).position(new Vector2f(2130, 1700)).velocity(new Vector2f()).build());
    }

}
