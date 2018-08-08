package yiome.projectend;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private GraphicManager graphicMng;
    private EntityManager entityMng;
    private SoundManager soundMng;
    private CanvasView canvasView;
    private Thread gameThread;
    private GameThread threadFilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //it is going to run in PORTRAIT only, no rotation possible
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        graphicMng = new GraphicManager();

        entityMng = new EntityManager(graphicMng);

        soundMng = new SoundManager(getBaseContext(), graphicMng);

        canvasView = new CanvasView(this, graphicMng);
        setFlags(canvasView);
        setContentView(canvasView);

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        threadFilling = new GameThread(canvasView, entityMng, soundMng, getApplicationContext(), display.getRefreshRate());
        gameThread = new Thread(threadFilling);
        gameThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFlags(canvasView);
        threadFilling.isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadFilling.isActive = false;
    }

    private void setFlags(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onBackPressed() {
        threadFilling.forceRestart();
    }
}
