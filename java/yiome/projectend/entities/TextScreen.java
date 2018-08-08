package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import yiome.projectend.gamemath.Vector2f;

public class TextScreen implements Entity, Trackable {

    public Queue <Vector2f> touches = new ConcurrentLinkedQueue();

    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    String text;
    int textColor;
    int backgroundColor;

    public boolean inflates = false;
    public boolean noReturn = false;
    float currentRadius = 0;

    public TextScreen(String msg, int fontSize, int textClr, int backgroundClr) {
        textColor = textClr;
        backgroundColor = backgroundClr;

        text = msg;
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(fontSize);
        textPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void sketch(Canvas cnv) {
        cnv.drawColor(backgroundColor);
        for(Vector2f v: touches) {
            cnv.drawCircle(v.x, v.y, currentRadius, textPaint);
        }
        textPaint.setColor(Color.GRAY);
        cnv.drawText(text, 4f, 4f, textPaint);
        textPaint.setColor(textColor);
        cnv.drawText(text, 0f, 0f, textPaint);
    }

    @Override
    public void update(float elapsedTime) {
        if(noReturn) {

            currentRadius += (currentRadius - 299f)*14f*elapsedTime;
            if(currentRadius > 3100f) currentRadius = 3100f;

        } else if(inflates) {

            if(currentRadius < 50f) currentRadius = 50f;
            currentRadius += (currentRadius)*3f*elapsedTime;
            if(currentRadius > 300f) noReturn = true;

        } else {

            currentRadius -= (currentRadius + 600f)*4f*elapsedTime;
            if(currentRadius < 10f) {
                currentRadius = 0f;
                touches.clear();
            }

        }
    }

    @Override
    public Vector2f getPosition() {
        return Vector2f.subtract(new Vector2f(0, 0), new Vector2f(-490, 0));
    }
}
