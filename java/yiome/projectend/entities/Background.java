package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import yiome.projectend.GraphicManager;
import yiome.projectend.gamemath.Vector2f;

public class Background implements Entity {

    final private int backgroundColor;
    final private Paint textPaint;
    public String[] strings;
    final private Vector2f[] positions;
    private float speed;
    private GraphicManager graphicMng;


    private Background(Builder b) {
        backgroundColor = b.backgroundColor;
        textPaint = b.textPaint;
        strings = b.strings;
        positions = b.positions;
        speed = b.speed;
        graphicMng = b.graphicMng;
    }

    @Override
    public void sketch(Canvas cnv) {
        cnv.drawColor(backgroundColor);
        if(graphicMng != null) {
            for(int i = 0; i < strings.length; i++) {
                Vector2f stdCoordinatePosition = Vector2f.add(positions[i], Vector2f.scalarMultiply(graphicMng.getCameraPosition(), speed));
                cnv.drawText(strings[i], stdCoordinatePosition.x, stdCoordinatePosition.y, textPaint);
            }
        }
    }

    @Override
    public void update(float elapsedTime) {

    }

    static public class Builder {
        private int backgroundColor = Color.WHITE;
        private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private String[] strings = null;
        private Vector2f[] positions = null;
        private float speed = 0f;
        private GraphicManager graphicMng = null;

        //no required fields
        public Builder() {
            textPaint.setColor(Color.GRAY);
            textPaint.setTextSize(80);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        }

        public Builder backgroundColor(int bgColor) { backgroundColor = bgColor; return this; }

        public Builder slidingText(String[] texts, Vector2f[] positionVectors, float slideSpeed, GraphicManager gMng) {
            if(texts.length == positionVectors.length) {
                strings = texts;
                positions = positionVectors;
                speed = slideSpeed;
                graphicMng = gMng;
            }
            return this;
        }

        public Builder paint(Paint paint) { textPaint = paint; return this; }

        public Background build() { return new Background(this); }
    }
}