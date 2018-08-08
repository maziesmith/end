package yiome.projectend;

import android.view.MotionEvent;

import java.util.Queue;

import yiome.projectend.entities.Player;
import yiome.projectend.gamemath.Vector2f;

class PlayerTouchManager {

    private Player player;
    private Queue <MotionEvent> touchEvents;
    private GraphicManager graphicMng;

    private Vector2f lastTouchPosition = new Vector2f(0, 0);
    private boolean interceptTouch = false;

    PlayerTouchManager(Player p, Queue <MotionEvent> tEvents, GraphicManager gMng) {
        player = p;
        touchEvents = tEvents;
        graphicMng = gMng;
    }

    void manage() {
        MotionEvent event;

        Vector2f touchPosition = new Vector2f(lastTouchPosition);
        touchPosition.set(graphicMng.transformVector(touchPosition));

        Vector2f pullVector = new Vector2f();
        pullVector.set(Vector2f.subtract(touchPosition, player.getPosition()));

        while((event = touchEvents.poll()) != null){

            touchPosition.set(event.getX(), event.getY());
            lastTouchPosition.set(touchPosition);
            touchPosition.set(graphicMng.transformVector(touchPosition));

            pullVector = Vector2f.subtract(touchPosition, player.getPosition());


            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    interceptTouch = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_UP:
                    if(interceptTouch == true) {
                        interceptTouch = false;
                        player.onPull(new Vector2f(pullVector));
                    }
                    break;

                default:
                    // Do nothing.
                    break;
            }

        }

        if(interceptTouch == true) player.CURRENT_DRAG = pullVector.length();
    }

}
