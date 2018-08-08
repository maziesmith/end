package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

import yiome.projectend.gamemath.Vector2f;

public class FillCircle implements Entity {
    Vector2f position = new Vector2f();
    float currentRadius = 30f;
    Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float fillSpeed;

    public FillCircle(Vector2f iniPosition, int color, float speed) {
        position.set(iniPosition);
        fillPaint.setColor(color);
        fillPaint.setStyle(Paint.Style.FILL);
        fillSpeed = speed;
    }

    @Override
    public void sketch(Canvas cnv) {
        cnv.drawCircle(position.x, position.y, currentRadius, fillPaint);
    }

    @Override
    public void update(float elapsedTime) {
        currentRadius += currentRadius*fillSpeed*elapsedTime;
        if(currentRadius > 2200f) currentRadius = 2200f;
    }
}
