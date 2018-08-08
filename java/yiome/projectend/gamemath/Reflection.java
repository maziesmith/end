package yiome.projectend.gamemath;

import yiome.projectend.entities.Ball;

public class Reflection {

    //https://en.wikipedia.org/wiki/Reflection_%28mathematics%29#Reflection_across_a_line_in_the_plane
    //returns vector after reflection
    public static Vector2f vectorAcross2DLine(Vector2f toReflect, Vector2f l) {
        return Vector2f.subtract(Vector2f.scalarMultiply(l, 2*(Vector2f.dotProduct(toReflect, l))/Vector2f.dotProduct(l, l)), toReflect);
        //dear java, what is wrong with operators overloading?
    }

    //this one MUTATES their positions and changes velocities
    public static void twoBalls(Ball ballA, Ball ballB) {
        float m1 = ballA.getMass();
        float m2 = ballB.getMass();
        float massSum = m1 + m2;
        float r1 = ballA.getRadius();
        float r2 = ballB.getRadius();
        Vector2f x1 = ballA.getPosition();
        Vector2f x2 = ballB.getPosition();

        //Correcting position
        Vector2f xH = Vector2f.subtract(x2, x1);
        if (xH.length() < 0.000001f) { // To avoid dividing by zero, just in case
            x1.add(new Vector2f(0.1f, 0.1f));
            xH = Vector2f.subtract(x2, x1);
        }

        float k = (r1 + r2)/xH.length();
        Vector2f displacement = Vector2f.scalarMultiply(xH, k - 1f);
        //MUTATING positions
        //the more mass ball has, the less it is displaced
        x1.set(Vector2f.subtract(x1, Vector2f.scalarMultiply(displacement, m2/(m1 + m2))));
        x2.set(Vector2f.add(x2, Vector2f.scalarMultiply(displacement, m1/(m1 + m2))));

        //Calculating new velocities
        Vector2f v1 = ballA.getVelocity();
        Vector2f v2 = ballB.getVelocity();

        Vector2f supportValue = Vector2f.subtract(x1, x2);

        float v1ComponentA = (2f * m2)/(m1 + m2);
        float v1ComponentB = Vector2f.dotProduct(Vector2f.subtract(v1, v2), supportValue) / (supportValue.length() * supportValue.length());
        Vector2f v1New = Vector2f.subtract(v1, Vector2f.subtract(x1, x2).scalarMultiply(v1ComponentA * v1ComponentB));

        supportValue.set(Vector2f.subtract(x2, x1));

        float v2ComponentA = (2f * m1)/(m1 + m2);
        float v2ComponentB = Vector2f.dotProduct(Vector2f.subtract(v2, v1), supportValue) / (supportValue.length() * supportValue.length());
        Vector2f v2New = Vector2f.subtract(v2, Vector2f.subtract(x2, x1).scalarMultiply(v2ComponentA * v2ComponentB));

        //set new velocities
        v1.set(v1New);
        v2.set(v2New);

        return;
    }
}
