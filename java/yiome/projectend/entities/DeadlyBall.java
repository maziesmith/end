package yiome.projectend.entities;

import android.graphics.Color;

import yiome.projectend.gamemath.Vector2f;

public class DeadlyBall extends Ball {

    public DeadlyBall(Ball.Builder b) {
        super(b);
        ballPaint.setColor(Color.argb(255, 216, 0, 0));
    }

    @Override
    public void onCollision(Ball b) {
        if(b instanceof DeadlyBall) {

        } else {
            desiredRadius -= b.desiredRadius*0.5f;
            if(!(b instanceof NiceBall)) b.destroy();
        }
    }
}
