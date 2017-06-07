package com.example.drawcurve;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.example.tool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Draw the curve
 */
public class TemperatureCurveView extends BezierCurveView {
    /**
     * Implement parameters,average the allocation between two points, bigger
     * will more meticulous
     */
    private static final String TAG = "TemperatureCurveView";
    private boolean mIsMetric;

    public TemperatureCurveView(Context context) {
        this(context, null);
    }

    public TemperatureCurveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemperatureCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TemperatureCurveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCurveColor(255, 255, 132, 0);
        setNoDataLineColor(255, 236, 233, 0);
    }

    private String temperatureText(int temp, boolean isMetric) {
        String text = String.valueOf(temp) + "\u00B0";
        if (isMetric) {
            text = String.valueOf((int) ((temp - 32) * 5 / 9)) + "\u00B0";
        }
        return text;
    }

    private void drawTarget(Canvas canvas, Point point, int y, int drawableRes, Paint paint) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
        canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2,
                point.y - (int) (bitmap.getHeight() * 0.6), paint);
        Rect bounds = new Rect();
        String text = temperatureText(y, mIsMetric);
        paint.getTextBounds(String.valueOf(text), 0, text.length(), bounds);
        int textY = point.y + (int) (bitmap.getHeight() * 0.4)
                + bounds.height();
        if (textY > this.getHeight()) {
            textY = point.y - (int) (1.0f * bounds.height());
        }

        canvas.drawText(
                String.valueOf(text),
                point.x - bounds.width() / 2,
                textY, paint);
    }

    @Override
    public void drawOnCurve(Canvas canvas, Paint paint, List<Integer> rawData, List<Point> allPoints) {
        ArrayList<Point> allPointsList = sortAllPoints(allPoints);
        ArrayList<Integer> sortList = sortRawData(rawData);
        if (allPointsList.size() > 0) {
            Point maxPoint = allPointsList.get(allPointsList.size() - 1);
            Point minPoint = allPointsList.get(0);
            int maxY = sortList.get(sortList.size() - 1);
            int minY = sortList.get(0);
            drawTarget(canvas, minPoint, minY, R.drawable.ic_weather_temperature_low, paint);
            drawTarget(canvas, maxPoint, maxY, R.drawable.ic_weather_temperature_high, paint);
            allPointsList.clear();
            sortList.clear();
        }
        Log.d(TAG, "drawOnCurve end");
    }

}
