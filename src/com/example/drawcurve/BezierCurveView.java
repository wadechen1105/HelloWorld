package com.example.drawcurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Draw the curve
 */
public class BezierCurveView extends View {
    /**
     * Implement parameters,average the allocation between two points, bigger
     * will more meticulous
     */
    private static final String TAG = "BezierCurveView";
    private static final int STEPS = 15;
    private static final int PADDING_DP = 5;
    private final static Comparator<Point> sComparator = new Comparator<Point>() {
        public int compare(Point p1, Point p2) {
            //cause the transform the coordinate, so small Y value is bigger actually
            return -(p1.y - p2.y);
        }
    };
    private Pen mPen;
    private Path mCurvePath;
    private List<Point> mAllPoints;
    private List<Integer> mPointsX;
    private List<Integer> mPointsY;
    private List<Integer> mRawDataList = new ArrayList<Integer>();
    private boolean mIsHasData = false;

    public BezierCurveView(Context context) {
        this(context, null);
    }

    public BezierCurveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initObj();
    }

    private void initObj() {
        Log.d(TAG, "initObj");
        mPen = new Pen();
        mCurvePath = new Path();
        mAllPoints = new LinkedList<Point>();
        mPointsX = new LinkedList<Integer>();
        mPointsY = new LinkedList<Integer>();
    }

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void clearpoints() {
        mRawDataList.clear();
        mAllPoints.clear();
        mPointsX.clear();
        mPointsY.clear();
    }

    private void refreshDrawPointer() {
        int listSize = mRawDataList.size();
        int padding = dpToPx(PADDING_DP);
        int spacingX = (getWidth() - padding * 2) / (listSize - 1);
        ArrayList<Integer> sortList = sortRawData(mRawDataList);
        int maxY = sortList.get(sortList.size() - 1);
        int minY = sortList.get(0);
        int viewHighet = this.getHeight();
        int drawHeight = this.getHeight() - (4 * padding);
        float extension = drawHeight / maxY;

        for (int i = 0; i < mRawDataList.size(); i++) {
            int positionY = padding;
            int positionX = padding + i * spacingX;
            if (maxY == minY) {
                positionY += drawHeight / 2;
                Log.v(TAG, "height positionY= " + positionY);
            } else {
                //we should change coordinate (0,0) at Left-down
                positionY += (int) (viewHighet - (mRawDataList.get(i) * extension));
            }
            setCalculatorPoint(positionX, positionY);
            mAllPoints.add(new Point(positionX, positionY));
            Log.v(TAG, "i = " + i +
                    " # maxY= " + maxY + " , minY = " + minY + " " +
                    "#  positionY = " + positionY + ", tY = " + mRawDataList.get(i));
        }
    }

    /**
     * cause the transform the coordinate, so small Y value is bigger actually
     */
    public ArrayList<Point> sortAllPoints(List<Point> list) {
        ArrayList<Point> tmpList = new ArrayList<>(list);
        Collections.sort(tmpList, sComparator);
        return tmpList;
    }

    public ArrayList<Integer> sortRawData(List<Integer> list) {
        ArrayList<Integer> sortList = new ArrayList<Integer>(list);
        Collections.sort(sortList);
        return sortList;
    }

    private void setCalculatorPoint(int x, int y) {
        Log.d(TAG, "mAllPoints x:" + x + " y:" + y);
        mPointsX.add(x);
        mPointsY.add(y);
    }

    /**
     * Calculate the curve.
     *
     * @param listPoint
     * @return
     */
    private List<Cubic> calculate(List<Integer> listPoint) {
        int n = listPoint.size() - 1;
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;
        /*
         * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
         * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
         * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])] by using
         * row operations to convert the matrix to upper triangular and then
         * back sustitution. The D[i] are the derivatives at the knots.
         */

        gamma[0] = 0.3f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (5 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (listPoint.get(1) - listPoint.get(0)) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (listPoint.get(i + 1) - listPoint.get(i - 1)) - delta[i - 1]) * gamma[i];
        }
        delta[n] = (3 * (listPoint.get(n) - listPoint.get(n - 1)) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        /* now compute the coefficients of the cubics */
        List<Cubic> cubics = new LinkedList<Cubic>();
        for (i = 0; i < n; i++) {
            Cubic c = new Cubic(listPoint.get(i),
                    D[i],
                    3 * (listPoint.get(i + 1) - listPoint.get(i)) - 2 * D[i] - D[i + 1],
                    2 * (listPoint.get(i) - listPoint.get(i + 1)) + D[i] + D[i + 1]);
            cubics.add(c);
        }
        return cubics;
    }

    /**
     * draw curve.
     */
    public void drawCurve(List<Integer> pointList) {
        clearpoints();
        Log.i(TAG, "draw curve, pointList size = " + pointList.size());
        mIsHasData = pointList.size() > 2 && getWidth() > 0;
        if (mIsHasData) {
            mRawDataList = pointList;
            refreshDrawPointer();
        }
        invalidate();
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw isHasData = " + mIsHasData);
        if (mIsHasData) {
            mPen.drawCurve(canvas);
            mPen.resetPenToDrawText();
            drawOnCurve(canvas, mPen, mRawDataList, mAllPoints);
        } else {
            mPen.drawNoDataLine(canvas);
        }
    }

    protected void drawOnCurve(Canvas canvas, Paint paint, List<Integer> rawData, List<Point> allPoints) {
    }

    /**
     * a,r,g,b
     */
    public void setNoDataLineColor(int a, int r, int g, int b) {

        mPen.setNoDataLineColor(new int[]{a, r, g, b});
    }

    /**
     * a,r,g,b
     */
    public void setCurveColor(int a, int r, int g, int b) {

        mPen.setCurveColor(new int[]{a, r, g, b});
    }

    private class Cubic {

        float a, b, c, d; /* a + b*u + c*u^2 +d*u^3 */

        public Cubic(float a, float b, float c, float d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        /**
         * evaluate cubic
         */
        public float eval(float u) {
            return (((d * u) + c) * u + b) * u + a;
        }

    }

    class Pen extends Paint {
        int[] mNoDataLineColor;
        int[] mCurveColor;
        int[] mTextColor;

        public Pen() {
            setAntiAlias(true);
            setStyle(Style.STROKE);
            mNoDataLineColor = new int[]{225, 50, 50, 50};
            mCurveColor = new int[]{225, 50, 50, 50};
            mTextColor = new int[]{255, 82, 82, 82};
            setTextSize(dpToPx(12));
            setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        }

        void configPaint(int[] color, int strokeWidthDP) {
            color(color);
            setStrokeWidth(dpToPx(strokeWidthDP));
        }

        void color(int[] color) {
            int a = color[0];
            int r = color[1];
            int g = color[2];
            int b = color[3];
            setARGB(a, r, g, b);
        }

        void resetPenToDrawText() {
            configPaint(mTextColor, 0);
        }

        void drawNoDataLine(Canvas canvas) {
            configPaint(mNoDataLineColor, 2);
            float x1 = 0;
            float y = getHeight() / 3.0f;
            float x2 = getWidth();
            canvas.drawLine(x1, y, x2, y, this);
        }

        void drawCurve(Canvas canvas) {
            configPaint(mCurveColor, 3);
            Log.i(TAG, "drawCurve");
            if (!mCurvePath.isEmpty()) {
                mCurvePath.reset();
            }

            if (mPointsX.size() > 1 && mPointsY.size() > 1) {
                List<Cubic> calculate_x = calculate(mPointsX);
                List<Cubic> calculate_y = calculate(mPointsY);
                int size = calculate_x.size();
                float x0 = 0;
                float y0 = calculate_y.get(0).eval(0);
                float x1 = calculate_x.get(0).eval(0);
                float y1 = calculate_y.get(0).eval(0);
                mCurvePath.moveTo(x0, y0);
                mCurvePath.lineTo(x1, y1);

                for (int i = 0; i < size; i++) {
                    for (int j = 1; j <= STEPS; j++) {
                        float u = j / (float) STEPS;
                        float lineToX = calculate_x.get(i).eval(u);
                        float lineToY = calculate_y.get(i).eval(u);
                        mCurvePath.lineTo(lineToX, lineToY);
                        if (i == (calculate_x.size() - 1) && j == STEPS) {
                            mCurvePath.lineTo(getWidth(), lineToY);
                        }
                    }
                }

                canvas.drawPath(mCurvePath, this);
            }
            Log.i(TAG, "drawCurve end");
        }

        public void setNoDataLineColor(int[] noDataLineColor) {
            mNoDataLineColor = noDataLineColor;
        }

        public void setCurveColor(int[] curveColor) {
            mCurveColor = curveColor;
        }
    }

}