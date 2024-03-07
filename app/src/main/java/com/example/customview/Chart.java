package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Chart extends View {
    private final List<Float> amplitudes = new ArrayList<>();
    private final List<RectF> spikes = new ArrayList<>();
    private final float radius = 6f;
    private final float width = 9f;
    private final float distance = 6f;
    private Paint paint;
    private float screenWidth = 0f;
    private float screenHeight = 400f;
    private int maxSpikes = 0;

    public Chart(Context context) {
        super(context);
        init();
    }

    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(15f);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        maxSpikes = (int) (screenWidth / (width + distance));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG", "onDraw: width " + getWidth() + "height: " + getHeight());
        spikes.forEach(rectF -> {
            canvas.drawRoundRect(rectF, radius, radius, paint);
        });
    }

    public void addAmplitude(float amp) {
        float norm = Math.min(amp / 15, 400);
        amplitudes.add(norm);
        spikes.clear();
        List<Float> amps = amplitudes.subList(Math.max(amplitudes.size() - maxSpikes, 0), amplitudes.size());
        for (int i = 0; i < amps.size(); i++) {
            float left = screenWidth - i * (width + distance);
            float top = screenHeight / 2 - amps.get(i) / 2;
            float right = left + width;
            float bottom = top + amps.get(i);

            spikes.add(new RectF(left, top, right, bottom));
        }
        invalidate();
    }

    public void clear() {
        amplitudes.clear();
        spikes.clear();
        invalidate();
    }
}