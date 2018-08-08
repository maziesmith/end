package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

import yiome.projectend.gamemath.Circle;
import yiome.projectend.gamemath.Vector2f;

public class Particle extends Circle implements Entity, Trackable {
    public boolean alive = true;
    private float timeLeft = 2f;
    Paint particlePaint = new Paint();
    Vector2f velocity = new Vector2f();

    public Particle(Vector2f initPosition, Vector2f initVelocity, float initRadius, int color, float timeToGo) {
        position.set(initPosition);
        velocity.set(initVelocity);
        radius = initRadius;
        particlePaint.setColor(color);
        particlePaint.setStyle(Paint.Style.FILL);
        timeLeft = timeToGo;
    }

    static public Vector2f randomVectorInCircle(Vector2f center, float radius) {
        Vector2f offSet = new Vector2f((float)Math.random()*2f - 1f, (float)Math.random());
        offSet.scalarMultiply((float)Math.random()*2f - 1f);
        offSet.normalize().scalarMultiply(radius*2f - (float)Math.random());
        return offSet.add(center);
    }

    static public Particle[] randomParticlesInCircle(Vector2f center, float posRadius, float partRadius, float speedFactor, int color, int amountOfParticles, float avgLife) {
        Vector2f pos;
        Vector2f vel;
        Particle[] output = new Particle[amountOfParticles];
        for(int i = 0; i < amountOfParticles; i++) {
            pos = randomVectorInCircle(center, posRadius);
            vel = randomVectorInCircle(new Vector2f(), speedFactor);
            output[i] = new Particle(pos, vel, partRadius, color, avgLife - 0.5f + (float)Math.random());
        }
        return output;
    }


    @Override
    public void sketch(Canvas cnv) {
        cnv.drawCircle(position.x, position.y, radius, particlePaint);
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    @Override
    public void update(float elapsedTime) {
        timeLeft -= elapsedTime;
        if(timeLeft < 0f) alive = false;
        position.add(Vector2f.scalarMultiply(velocity, elapsedTime));
    }
}
