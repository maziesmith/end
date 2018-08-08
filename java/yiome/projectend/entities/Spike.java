package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import yiome.projectend.gamemath.Polygon;
import yiome.projectend.gamemath.Vector2f;

public class Spike extends Polygon implements Entity, Trackable {
    public boolean alive = true;

    Path spikePath;
    Paint spikePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    Path shadowPath1;
    Path shadowPath2;
    Path shadowPath3;
    Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Spike(Vector2f[] initialVertices) {
        vertices = new Vector2f[initialVertices.length];
        for(int i = 0; i < initialVertices.length; i++) {
            vertices[i] = new Vector2f(initialVertices[i]);
        }
        spikePath = Polygon.createPath(this, new Vector2f());

        shadowPath1 = Polygon.createPath(this, new Vector2f(2f, 2f));
        shadowPath2 = Polygon.createPath(this, new Vector2f(4f, 4f));
        shadowPath3 = Polygon.createPath(this, new Vector2f(6f, 6f));

        spikePaint.setStyle(Paint.Style.FILL);
        spikePaint.setColor(Color.argb(255, 216, 0, 0));
        shadowPaint.setStyle(Paint.Style.FILL);
    }

    public void onCollision(Ball b) {
        if(!(b instanceof DeadlyBall) && !(b instanceof NiceBall)) b.destroy();
    }

    protected void sketchShadow(Canvas cnv) {
        shadowPaint.setColor(Color.argb(190, 64, 64, 64));
        cnv.drawPath(shadowPath1, shadowPaint);
        shadowPaint.setColor(Color.argb(128, 64, 64, 64));
        cnv.drawPath(shadowPath2, shadowPaint);
        shadowPaint.setColor(Color.argb(64, 64, 64, 64));
        cnv.drawPath(shadowPath3, shadowPaint);
    }

    @Override
    public void sketch(Canvas cnv) {
        sketchShadow(cnv);
        cnv.drawPath(spikePath, spikePaint);
        spikePaint.setColor(Color.RED);
    }

    public void destroy() {
        alive = false;
    }

    @Override
    public void update(float elapsedTime) {

    }

    /*
    * Useful Spike placement functions
    * */

    static private final float BASE_RATIO = 1.3f;
    static public final float TO_FIT = 0f;

    static public Vector2f[] stickOnWall(Wall w, float placeOnWall, float spikeHeight, boolean rotate) {
        float baseLength = spikeHeight/BASE_RATIO;
        Vector2f buildOn = w.getDirectionalVector().normalize();
        Vector2f baseCenter = Vector2f.add(w.pointA ,Vector2f.scalarMultiply(buildOn,placeOnWall));
        Vector2f v1 = Vector2f.add(baseCenter, Vector2f.scalarMultiply(buildOn, -0.5f * baseLength));
        Vector2f v2 = Vector2f.add(baseCenter, Vector2f.scalarMultiply(buildOn, 0.5f * baseLength));
        Vector2f tipPoint = Vector2f.perpendicularTo(buildOn).scalarMultiply(spikeHeight);
        if(rotate) tipPoint.scalarMultiply(-1f);
        Vector2f v3 = Vector2f.add(baseCenter, tipPoint);
        return new Vector2f[] {v1, v2, v3};
    }

    static public Spike[] spikyWall(Wall w, float startingLength, float endingLength, float spacing, float height, boolean rotate) {
        if(spacing == TO_FIT) spacing = height/BASE_RATIO;

        int amount = (int)((endingLength - startingLength)/spacing);
        Spike[] output = new Spike[amount];
        for(int i = 0; i < amount; i++) {
            output[i] = new Spike(stickOnWall(w, startingLength + i * spacing, height, rotate));
        }
        return output;
    }

    @Override
    public Vector2f getPosition() { return vertices[0]; }
}
