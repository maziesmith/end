package yiome.projectend.gamemath;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class Polygon {
    public Vector2f[] vertices = new Vector2f[0];

    static public Path createPath(Polygon p, Vector2f offSet) {
        Path polygonPath = new Path();
        polygonPath.moveTo(p.vertices[0].x + offSet.x, p.vertices[0].y + offSet.y);
        for(int i = 1; i < p.vertices.length; i++) {
            polygonPath.lineTo(p.vertices[i].x + offSet.x, p.vertices[i].y + offSet.y);
        }
        polygonPath.lineTo(p.vertices[0].x + offSet.x, p.vertices[0].y + offSet.y);
        return polygonPath;
    }
}
