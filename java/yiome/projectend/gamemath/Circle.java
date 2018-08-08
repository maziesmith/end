package yiome.projectend.gamemath;

public class Circle {

    static public float area(float radius) {
        return radius*radius*3.14f;
    }

    public Vector2f position = new Vector2f();

    public float radius = 16f;

}
