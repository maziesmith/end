package yiome.projectend.entities;

import android.graphics.Color;

import yiome.projectend.gamemath.Vector2f;

public class NiceBall extends Ball {

    public NiceBall(Ball.Builder b) {
        super(b);
        ballPaint.setColor(Color.argb(255, 0, 128, 0));
    }

    @Override
    public void onCollision(Spike s) {
        s.destroy();
    }

    @Override
    public void onCollision(Ball b) {
        if(b instanceof DeadlyBall) {
            this.desiredRadius -= 0.2f * b.desiredRadius;
            b.destroy();
        }
    }
}
