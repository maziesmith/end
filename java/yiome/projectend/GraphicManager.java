package yiome.projectend;

import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.Collection;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

import yiome.projectend.entities.Sketchable;
import yiome.projectend.entities.Trackable;
import yiome.projectend.entities.Updatable;
import yiome.projectend.gamemath.Vector2f;

public class GraphicManager implements Updatable {

    private Queue <Sketchable> toSketchQueue = new ConcurrentLinkedQueue<>();

    private Matrix transformationMatrix;
    private Deque<Matrix> lastMatrices = new LinkedBlockingDeque<>();

    private Trackable toFollow = null;
    private Vector2f goTo = null;
    private float cameraSpeedFactor = 5f;
    private Vector2f cameraPosition = new Vector2f(0, 0);
    private int cameraType = 1;

    public static final int FAST_WHEN_FAR = 1;
    public static final int SLOW_WHEN_FAR = 2;

    private Vector2f displayDimensions;
    private float scaleFactor;

    volatile public boolean DIRTY_FLAG = false;

    GraphicManager() {
        transformationMatrix = new Matrix();
        scaleFactor = 0;
    }

    public void setDefaults() {
        cameraSpeedFactor = 5f;
        cameraType = 1;
        cameraPosition.set(0, 0);
        toFollow = null;
        goTo = null;
    }

    public void setToFollow(Trackable toFollow) {
        this.toFollow = toFollow;
    }

    public void setGoTo(Vector2f whereToGo) {
        goTo = whereToGo;
    }

    public Vector2f getCameraPosition() { return new Vector2f(cameraPosition); }

    public void placeCamera(Vector2f newPlace) {
        cameraPosition.set(newPlace);
    }

    public void changeSpeed(float newSpeed) {
        cameraSpeedFactor = newSpeed;
    }

    public void setCameraType(int newType) {
        cameraType = newType;
    }

    void setScale(Vector2f viewDimensions) {
        displayDimensions = viewDimensions;
        scaleFactor = displayDimensions.x / 1080.f;
    }

    private void manageLastMatrices(Matrix m) {
        if(lastMatrices.size() < 2){
            lastMatrices.addFirst(m);
        } else {
            lastMatrices.peekLast();
            lastMatrices.addFirst(m);
        }
    }

    Vector2f transformVector(Vector2f v) {
        if(lastMatrices.isEmpty()) return new Vector2f(v);

        float [] point = new float[] {v.x, v.y};

        Matrix inverseMatrix = new Matrix(lastMatrices.peekFirst());
        inverseMatrix.invert(inverseMatrix);

        inverseMatrix.mapPoints(point);
        return new Vector2f(point[0], point[1]);
    }

    private Vector2f getDestination() {
        Vector2f destination = null;
        //it's safe because later we create new Vector out of it
        if(goTo != null) {
            if(Vector2f.subtract(cameraPosition, goTo).length() < 5f) goTo = null;
            destination = goTo;
            //if true, quest completed
        }
        else if(toFollow != null) destination = toFollow.getPosition();

        return destination;
    }

/*    private float getScaleBonus() {
        Vector2f dest = getDestination();
        if(dest == null) return 0f;
        float len = Vector2f.subtract(cameraPosition, getDestination()).length();
        float scaleBonus = (-len)/8000f;
        if(scaleFactor + scaleBonus < scaleFactor/5f) return scaleFactor*(4f/5f);
        else return scaleBonus;
    }*/

    void sketchOn(Canvas cnv) {
        while (DIRTY_FLAG) {}
        DIRTY_FLAG = true;

        transformationMatrix.reset();
        transformationMatrix.postTranslate(-cameraPosition.x + displayDimensions.x/(2* scaleFactor),-cameraPosition.y + displayDimensions.y/(2* scaleFactor));
        transformationMatrix.postScale(scaleFactor, scaleFactor);
        manageLastMatrices(transformationMatrix);

        cnv.concat(transformationMatrix);

        for(Sketchable s: toSketchQueue) {
            if(s instanceof Trackable) if(Vector2f.subtract(((Trackable) s).getPosition(), cameraPosition).length() > 1350f) continue;
            s.sketch(cnv);
        }

        DIRTY_FLAG = false;
    }

    @Override
    public void update(float elapsedTime) {
        Vector2f destination = getDestination();
        if(destination == null) return;

        Vector2f direction = Vector2f.subtract(destination, cameraPosition);

        switch (cameraType) {
            case FAST_WHEN_FAR:
                direction.scalarMultiply(cameraSpeedFactor*elapsedTime);
                break;
            case SLOW_WHEN_FAR:
                //Not really working as intended, avoid for now
                direction.scalarMultiply(elapsedTime);
                break;
        }

        cameraPosition.add(direction);

    }

    void addSketchable(Sketchable s) {
        if(toSketchQueue.contains(s) == false) toSketchQueue.add(s);
    }

    void addAllSketchables(Collection <? extends Sketchable> newSketchables) {
        toSketchQueue.addAll(newSketchables);
    }

    void removeSketchable(Sketchable s) {
        try {
            toSketchQueue.remove(s);
        } catch (Exception e) { }
    }

    void emptySketchables() {
        toSketchQueue.clear();
    }

}