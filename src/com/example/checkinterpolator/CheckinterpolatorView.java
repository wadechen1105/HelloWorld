
package com.example.checkinterpolator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class CheckinterpolatorView extends View {

    private static final int POINT_COUNTS = 1000;
    private ArrayList<Interpolator> mList = new ArrayList<Interpolator>();
    Interpolator mInterpolator;
    Paint mCurvePaint;
    Paint mTextPaint;
    public static final int OBJ_COUNT = 5;
    private boolean startDraw = false;

    public CheckinterpolatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurvePaint = new Paint(Paint.DITHER_FLAG);
        mCurvePaint.setAntiAlias(true);
        mCurvePaint.setStrokeWidth(5);
        mCurvePaint.setColor(Color.BLACK);

        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(50);

        for (int i = 0; i < OBJ_COUNT; i++) {
            mList.add(selectInterpolator(i));
        }
    }

    private Interpolator selectInterpolator(int i) {
        Interpolator interpolator = null;
        switch (i) {
            case 0:
                interpolator = new AccelerateInterpolator();
                break;
            case 1:
                interpolator = new BounceInterpolator();
                break;
            case 2:
                interpolator = new DecelerateInterpolator();
                break;
            case 3:
                interpolator = new AccelerateDecelerateInterpolator();
                break;
            case 4:
                interpolator = new LinearInterpolator();
                break;

        }
        return interpolator;
    }

    public String getInterpolatorName(int itemPosition) {
        return mList.get(itemPosition).getClass().getSimpleName();
    }

    public void drawCurve(int itemPosition) {
        mInterpolator = mList.get(itemPosition);
        invalidate();
        startDraw = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!startDraw) {
            return;
        }

        int x_coordinate = getWidth();
        int y_coordinate = 700;
        float lineLength = 30.0f;

        canvas.save();

        for (int i = 0; i < POINT_COUNTS; i++) {
            float x = i * 1.0f / POINT_COUNTS;
            float y = mInterpolator.getInterpolation(x);
            canvas.drawPoint(x_coordinate * x, (getHeight() - (y_coordinate * y)), mCurvePaint);
        }

        for (int i = 1; i <= 4; i++) {
            float poistion = i * 25;
            // String strY = String.valueOf("|");
            // String strX = String.valueOf("-");
            float x = getWidth() * (poistion / 100);
            float y = getHeight() - (y_coordinate * (poistion / 100));
            canvas.drawLine(x, getHeight(), x, getHeight() - lineLength, mTextPaint);
            canvas.drawLine(0.0f, y, lineLength, y, mTextPaint);
            // canvas.drawText(strY, x, getHeight(), mTextPaint);
            // canvas.drawText(strX, 0, y, mTextPaint);
        }

        canvas.restore();
        startDraw = false;


    }
}
