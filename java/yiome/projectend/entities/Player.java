package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import yiome.projectend.gamemath.Vector2f;

public class Player extends Ball {

    Paint speedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint maxExceeded = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float SPEED_MULTIPLIER = 3f;
    private final float MAX_POWER = 450f;

    private float CURRENT_POWER = 0f;
    public final float POWER_RECOVERY_SPEED = 250f;

    volatile public float CURRENT_DRAG = 0f;
    public boolean haveWon = false;

    public Player(Ball.Builder b) {
        super(b);
        ballPaint.setColor(Color.BLACK);
        speedPaint.setStyle(Paint.Style.FILL);

        maxExceeded.setStyle(Paint.Style.STROKE);
        maxExceeded.setStrokeWidth(5);
        maxExceeded.setColor(Color.argb(100, 0, 0, 0));
    }

    public void onPull(Vector2f pullVector) {
        if(pullVector.length() > CURRENT_POWER) {
            pullVector.normalize();
            pullVector.scalarMultiply(CURRENT_POWER);
        }
        CURRENT_POWER -= pullVector.length();
        velocity.add(pullVector.scalarMultiply(SPEED_MULTIPLIER));
        CURRENT_DRAG = 0f;
    }

    public void destroy() {
        alive = false;
    }

    @Override
    public void update(float elapsedTime) {
        updatePosition(elapsedTime);
        CURRENT_POWER += elapsedTime*POWER_RECOVERY_SPEED;
        if(CURRENT_POWER > MAX_POWER) CURRENT_POWER = MAX_POWER;
    }

    @Override
    public void sketch(Canvas cnv) {
        if(radius > 0f) {
            float DRAG_BUFFER = CURRENT_DRAG;
            if(DRAG_BUFFER >= CURRENT_POWER) {
                DRAG_BUFFER = CURRENT_POWER;
            }
            cnv.drawCircle(position.x, position.y, CURRENT_POWER, maxExceeded);
            setSpeedColor(DRAG_BUFFER);
            cnv.drawCircle(position.x, position.y, DRAG_BUFFER, speedPaint);

            sketchShadow(cnv);
            cnv.drawCircle(position.x, position.y, radius, ballPaint);
        }
    }

    private void setSpeedColor(float len){
        final float ALPHA = 90f;
        final float MAX_INTENSITY = 255f;
        final float DIVIDER = (MAX_POWER)/3f;
        int state = (int)(len/DIVIDER);
        float intensity = (len - (state*DIVIDER))/DIVIDER*MAX_INTENSITY;

        switch (state) {
            case 0:
                speedPaint.setColor(Color.argb((int)ALPHA, 0, (int)MAX_INTENSITY, 0));
                break;
            case 1:
                speedPaint.setColor(Color.argb((int)ALPHA, (int)intensity, (int)MAX_INTENSITY, 0));
                break;
            case 2:
                speedPaint.setColor(Color.argb((int)ALPHA, (int)MAX_INTENSITY, (int)MAX_INTENSITY - (int)intensity, 0));
                break;
            default:
                speedPaint.setColor(Color.argb((int)ALPHA, (int)MAX_INTENSITY, 0, 0));
                break;
        }
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    @Override
    public Vector2f getVelocity() {
        return velocity;
    }
}
