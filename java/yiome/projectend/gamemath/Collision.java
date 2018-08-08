package yiome.projectend.gamemath;

public class Collision {

    //Point & Circle
    static public boolean check(Vector2f point, Circle circle) {
        if(Vector2f.subtract(circle.position, point).length() <= circle.radius) return true;
        else return false;
    }

    //Point & Line
    static public boolean check(Vector2f point, Line line) {
        float l1 = Vector2f.subtract(point, line.pointA).length();
        float l2 = Vector2f.subtract(point, line.pointB).length();
        float l3 = line.getDirectionalVector().length();
        final float BUFFER = 0.1f;
        if(l1 + l2 >= l3 - BUFFER && l1 + l2 <= l3 + BUFFER){
            return true;
        } else return false;
    }

    //Circle & Line
    static public Vector2f check(Circle circle, Line line) {
        //if we touch any of the ends just leave, its colliding
        if(check(line.pointA, circle)) return new Vector2f(line.pointA);
        else if(check(line.pointB, circle)) return new Vector2f(line.pointB);

        //calculating closest point
        float x1, y1, x2, y2;
        x1 = line.pointA.x;
        y1 = line.pointA.y;
        x2 = line.pointB.x;
        y2 = line.pointB.y;

        float len = line.getDirectionalVector().length();
        Vector2f v1 = Vector2f.subtract(circle.position, line.pointA);
        float dot = Vector2f.dotProduct(v1, line.getDirectionalVector())/(len*len);

        Vector2f closestPoint = new Vector2f(x1 + (dot * (x2-x1)), y1 + (dot * (y2-y1)));

        //if found point don't lay on the line they do not collide
        if(check(closestPoint, line)) {
            if(Vector2f.subtract(closestPoint, circle.position).length() < circle.radius) {
                return closestPoint;
            }
        }
        return null;
    }

    //Circle & Circle
    static public boolean check(Circle circleA, Circle circleB) {
        if(Vector2f.subtract(circleA.position, circleB.position).length() <= circleA.radius + circleB.radius) return true;
        else return false;
    }

    //Point & Polygon
    static public boolean check(Vector2f point, Polygon polygon) {
        boolean collision = false;
        Vector2f vC = new Vector2f();
        Vector2f vN = new Vector2f();

        for(int next, current = 0; current < polygon.vertices.length; current++) {
            next = current + 1;
            if(next == polygon.vertices.length) next = 0;

            vC.set(polygon.vertices[current]);
            vN.set(polygon.vertices[next]);
            float pX = point.x;
            float pY = point.y;

            if( ((vC.y > pY) != (vN.y > pY)) && (pX - vC.x < (vN.x - vC.x) * (pY - vC.y) / (vN.y - vC.y)) ) {
                collision = !collision;
            }
        }
        return collision;
    }

    //Circle & Polygon
    static public boolean check(Circle circle, Polygon polygon){
        //it is more likely that boundary of circle touches the polygon
        Line currentLine = new Line();
        for(int next, current = 0; current < polygon.vertices.length; current++) {
            next = current + 1;
            if(next == polygon.vertices.length) next = 0;

            currentLine.pointA.set(polygon.vertices[current]);
            currentLine.pointB.set(polygon.vertices[next]);
            if(check(circle, currentLine) != null) return true;
        }
        //than circle is fully inside // if(check(circle.position, polygon)) return true;
        return false;
    }

}
