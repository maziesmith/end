package yiome.projectend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import yiome.projectend.gamemath.Vector2f;

public class CanvasView extends View {

    private GraphicManager graphicMng;

    Queue <MotionEvent> touchEventsQueue;

    CanvasView(Context context, GraphicManager mng) {
        super(context);
        graphicMng = mng;
        touchEventsQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        graphicMng.setScale(new Vector2f(w, h));
    }

    @Override
    protected void onDraw(Canvas cnv) {
        super.onDraw(cnv);
        graphicMng.sketchOn(cnv);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchEventsQueue.add(event);
        return true;
    }
}

