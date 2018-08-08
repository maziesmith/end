package yiome.projectend;

import android.view.MotionEvent;

import java.util.Queue;

import yiome.projectend.entities.TextScreen;
import yiome.projectend.gamemath.Vector2f;

public class StartTouchManager {

    TextScreen playScr;
    private Queue <MotionEvent> touchEvents;
    private GraphicManager graphicMng;


    StartTouchManager(TextScreen textScreen, Queue<MotionEvent> tEvents, GraphicManager gMng) {
        playScr = textScreen;
        touchEvents = tEvents;
        graphicMng = gMng;
    }

    void manage() {
        MotionEvent event;

        Vector2f touchPosition = new Vector2f();

        while((event = touchEvents.poll()) != null){

            touchPosition.set(event.getX(), event.getY());
            touchPosition.set(graphicMng.transformVector(touchPosition));

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    playScr.inflates = true;
                    playScr.touches.add(new Vector2f(touchPosition));
                    break;

                case MotionEvent.ACTION_UP:
                    playScr.inflates = false;
                    break;
            }

        }
    }

}
