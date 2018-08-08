package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import yiome.projectend.gamemath.Circle;
import yiome.projectend.gamemath.Vector2f;

public class Ball extends Circle implements Entity, Trackable {
    public boolean alive = true;
    Vector2f velocity = new Vector2f();
    final private float mass;
    final private float frictionFactor;
    public float desiredRadius;
    public Paint ballPaint;
    Paint shadowPaint;


    protected Ball(Ball.Builder b) {
        position.set(b.position);
        velocity.set(b.velocity);
        mass = b.mass;
        radius = b.radius;
        desiredRadius = b.radius;
        frictionFactor = b.frictionFactor;
        ballPaint = new Paint(b.ballPaint);
        shadowPaint = new Paint(b.shadowPaint);
    }

    public void onCollision(Ball b){

    }

    public void onCollision(Spike s){

    }

    protected void sketchShadow(Canvas cnv) {
        shadowPaint.setColor(Color.argb(190, 64, 64, 64));
        cnv.drawCircle(position.x + 2f, position.y + 2f, radius + 2f, shadowPaint);
        shadowPaint.setColor(Color.argb(128, 64, 64, 64));
        cnv.drawCircle(position.x + 4f, position.y + 4f, radius + 4f, shadowPaint);
        shadowPaint.setColor(Color.argb(64, 64, 64, 64));
        cnv.drawCircle(position.x + 6f, position.y + 6f, radius + 6f, shadowPaint);
    }

    public float getRadius() {
        return radius;
    }

    public float getMass() {
        return mass;
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    @Override
    public void sketch(Canvas cnv) {
        sketchShadow(cnv);
        cnv.drawCircle(position.x, position.y, radius, ballPaint);
    }

    public void destroy() {
        desiredRadius = 0f;
    }

    public void updateRadius(float elapsedTime) {
        radius += ((desiredRadius - radius) > 0 ? 1 : -1)*elapsedTime*32f;
        if(radius <= 0f) alive = false;
    }

    protected void updatePosition(float elapsedTime) {
        //apply friction to velocity
        float newLength = velocity.length();
        newLength -= elapsedTime*frictionFactor;
        velocity.normalize().scalarMultiply(newLength);
        //move
        position.add(Vector2f.scalarMultiply(velocity, elapsedTime));
    }

    @Override
    public void update(float elapsedTime) {
        updatePosition(elapsedTime);
        updateRadius(elapsedTime);
    }

    static public class Builder {
        private Vector2f position = new Vector2f();
        private Vector2f velocity = new Vector2f();
        private float radius = 40f;
        private float mass = Circle.area(40f);
        private float frictionFactor = 250f;
        private Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public Builder() {
            ballPaint.setColor(Color.GRAY);
            ballPaint.setStyle(Paint.Style.FILL);
            shadowPaint.setStyle(Paint.Style.FILL);
        }

        public Builder position(Vector2f initPosition) { position.set(initPosition); return this; }
        public Builder velocity(Vector2f initVelocity) { velocity.set(initVelocity); return this; }
        public Builder radius(float initRadius) { radius = initRadius; mass = Circle.area(initRadius); return this; }
        public Builder friction(float initFrictionFactor) { frictionFactor = initFrictionFactor; return this; }
        //public Builder color(int initColor) { ballPaint.setColor(initColor); return this; } //not used

        public Ball build() { return new Ball(this); }

    }
}
