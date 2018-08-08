package yiome.projectend;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import yiome.projectend.gamemath.Vector2f;

public class SoundManager {
    private SoundPool soundPool;
    private GraphicManager graphicMng;
    private int start;
    private int klak;
    private int pipe;
    private int lose;
    private int win;
    private int pop;

    public SoundManager(Context context, GraphicManager graphicManager) {
        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        graphicMng = graphicManager;
        start = soundPool.load(context, R.raw.start2, 1);
        klak = soundPool.load(context, R.raw.klak, 1);
        pipe = soundPool.load(context, R.raw.pipe, 1);
        lose = soundPool.load(context, R.raw.click, 1);
        win = soundPool.load(context, R.raw.win, 1);
        pop = soundPool.load(context, R.raw.pop, 1);
    }

    private float calculateSoundStrength(Vector2f soundPosition) {
        float distance = Vector2f.subtract(graphicMng.getCameraPosition(), soundPosition).length();
        float strength = 1f - distance/2000f;
        if(strength < 0f) return 0f;
        if(strength > 1f) return 1f;
        else return strength;
    }

    public void playStart() {
        soundPool.play(start, 0.8f, 0.8f, 0, 0, 1);
    }

    public void playKlak(Vector2f soundPosition) {
        float strength = calculateSoundStrength(soundPosition);
        if(strength > 0f) soundPool.play(klak, strength, strength, 0, 0, 0.7f + (float)Math.random()/4f);
    }

    public void playPipe(Vector2f soundPosition) {
        final float POWER_OFFSET = 0.3f;
        float strength = calculateSoundStrength(soundPosition);
        if(strength > POWER_OFFSET) soundPool.play(pipe, strength - POWER_OFFSET, strength - POWER_OFFSET, 0, 0, 1.75f + (float)Math.random()/4f);
    }

    public void playLose(){
        soundPool.play(lose, 1f, 1f, 0, 0, 1);
    }

    public void playWin(){
        soundPool.play(win, 1f, 1f, 0, 0, 1);
    }

    public void playPop(Vector2f soundPosition) {
        float strength = calculateSoundStrength(soundPosition);
        if(strength > 0f) soundPool.play(pop, strength, strength, 0, 0, 1f + (float)Math.random()/4f);
    }

}
