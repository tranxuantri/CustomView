package com.example.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.customview.entity.Entry;

import java.util.List;

public class Chart extends View {
    private Paint paint, paint2;
    private List<Entry> mEntryList;

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
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(2f);
        paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setStrokeWidth(3f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG", "onDraw: width " + getWidth() + "height: " + getHeight());

        drawXAxis(canvas);
        drawYAxis(canvas);
        if (mEntryList != null && mEntryList.size() > 0) {
            for (int i = 0; i < mEntryList.size() - 1; i++) {
                float startX1 = Math.round(getWidth() * mEntryList.get(i).getX() * 0.02);
                float startY1 = Math.round((getHeight() - 50) * (mEntryList.get(i).getY() / 1000));
                float endX1 = Math.round(getWidth() * mEntryList.get(i + 1).getX() * 0.02);
                float endY1 = Math.round((getHeight() - 50) * (mEntryList.get(i + 1).getY() / 1000));
                canvas.drawLine(startX1, startY1, endX1, endY1, paint);
            }
        }
    }

    private void drawXAxis(Canvas canvas) {
        canvas.drawLine(0, getHeight() - 50, getWidth(), getHeight() - 50, paint2);
        paint.setTextSize(50);
        canvas.drawText("0", 0, getHeight(), paint);
        canvas.drawText("50", getWidth() - 50, getHeight(), paint);
    }

    private void drawYAxis(Canvas canvas) {
        canvas.drawLine(0, 0, 0, getHeight() - 50, paint2);
    }

    public void setData(List<Entry> entryList) {
        mEntryList = entryList;
        invalidate();
    }

}