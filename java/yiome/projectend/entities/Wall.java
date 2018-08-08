package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import yiome.projectend.gamemath.Line;
import yiome.projectend.gamemath.Reflection;
import yiome.projectend.gamemath.Vector2f;

public class Wall extends Line implements Entity {

    public Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Wall(Vector2f start, Vector2f end) {
        pointA = new Vector2f(start);
        pointB = new Vector2f(end);

        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(4);
    }

    public void onCollide(Ball ball, Vector2f closestPoint) {

        Vector2f v = ball.getVelocity();
        Vector2f l = getDirectionalVector();

        ball.getVelocity().set(Reflection.vectorAcross2DLine(v, l));

        l = Vector2f.subtract(ball.getPosition(), closestPoint);
        l.normalize();
        l.scalarMultiply(ball.getRadius());
        l.add(closestPoint);

        ball.position.set(l);
    }

    @Override
    public void sketch(Canvas cnv) {
        cnv.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, linePaint);
    }

    @Override
    public void update(float elapsedTime) {

    }
}
