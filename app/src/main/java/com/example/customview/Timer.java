package com.example.customview;

import android.os.Handler;
import android.os.Looper;

public class Timer {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private long duration = 0L;
    private final long delay = 50L;

    public Timer(OnTimeTickListener listener) {
        runnable = () -> {
            duration += delay;
            handler.postDelayed(runnable, delay);
            listener.onTimerTick(format());
        };
    }

    public void start() {
        handler.postDelayed(runnable, delay);
    }

    public void pause() {
        handler.removeCallbacks(runnable);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        duration = 0L;
    }

    private String format() {
        long millis = duration % 1000;
        long second = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = (duration / (1000 * 60 * 60));

        return hours > 0 ?
                String.format("%02d:%02d:%02d,%02d", hours, minutes, second, millis/10) :
                String.format("%02d:%02d,%02d", minutes, second, millis/10);
    }

    public interface OnTimeTickListener {
        void onTimerTick(String duration);
    }
}