package yiome.projectend.gamemath;

import android.graphics.Point;
import android.graphics.PointF;

public class Vector2f extends PointF {

    static public Vector2f normalize(Vector2f v1) {
        Vector2f newVector = new Vector2f();

        float length = v1.length();
        if (length != 0) {
            newVector.set(v1.x/length, v1.y/length);
        }
        return newVector;
    }

    static public float dotProduct(Vector2f A, Vector2f B) {
        return A.x * B.x + A.y * B.y;
    }

    static public Vector2f add(Vector2f v1, Vector2f v2) {
        return new Vector2f(v1.x + v2.x, v1.y + v2.y);
    }

    static public Vector2f subtract(Vector2f v1, Vector2f v2) {
        return new Vector2f(v1.x - v2.x, v1.y - v2.y);
    }

    static public Vector2f scalarMultiply(Vector2f v1, float scalar) {
        return new Vector2f(v1.x * scalar, v1.y * scalar);
    }

    static public Vector2f perpendicularTo(Vector2f v) {
        return new Vector2f(-v.y, v.x);
    }

    public Vector2f() {
        this.set(0, 0);
    }

    public Vector2f(float xInit, float yInit) {
        x = xInit;
        y = yInit;
    }

    public Vector2f(PointF initializer) {
        this.set(initializer);
    }

    public PointF getPointF() {
        return new PointF(x, y);
    }

    public Point getPoint() {
        return new Point((int)x, (int)y);
    }

    public Vector2f set(Vector2f v2) {
        this.x = v2.x;
        this.y = v2.y;
        return this;
    }

    public Vector2f add(Vector2f v2) {
        this.x += v2.x;
        this.y += v2.y;
        return this;
    }

    public Vector2f subtract(Vector2f v2) {
        this.x -= v2.x;
        this.y -= v2.y;
        return this;
    }

    public Vector2f scalarMultiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2f normalize() {
        float length = length();
        if (length != 0) {
            set(x/length, y/length);
        }
        return this;
    }

}
