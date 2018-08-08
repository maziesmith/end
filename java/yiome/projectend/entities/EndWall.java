package yiome.projectend.entities;

import android.graphics.Canvas;
import android.graphics.Color;

import yiome.projectend.gamemath.Vector2f;

public class EndWall extends Wall {

    public EndWall(Vector2f start, Vector2f end) {
        super(start, end);
        linePaint.setColor(Color.argb(255, 0, 255, 0));
        linePaint.setStrokeWidth(12);
    }

    @Override
    public void sketch(Canvas cnv) {
        linePaint.setColor(Color.argb(255, 0, 104, 0));
        cnv.drawLine(pointA.x, pointA.y + 8f, pointB.x, pointB.y + 8f, linePaint);
        linePaint.setColor(Color.argb(255, 0, 168, 0));
        cnv.drawLine(pointA.x, pointA.y + 4f, pointB.x, pointB.y + 4f, linePaint);
        linePaint.setColor(Color.argb(255, 0, 200, 0));
        cnv.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, linePaint);
    }

    @Override
    public void onCollide(Ball ball, Vector2f closestPoint) {
        if(ball instanceof Player) {
            ((Player)ball).haveWon = true;
        }
        else super.onCollide(ball, closestPoint);
    }
}
