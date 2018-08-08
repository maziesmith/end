package yiome.projectend.gamemath;

public class Line {

    public Vector2f pointA = new Vector2f();

    public Vector2f pointB = new Vector2f();

    public Vector2f getDirectionalVector() {
        return Vector2f.subtract(pointB, pointA);
    }

}
