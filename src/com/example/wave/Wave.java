/*****************************************************************************
 * Copyright (C) 2011 Acer, Inc. All Rights Reserved.
 *
 * This program is an unpublished copyrighted work which is proprietary
 * to Acer, Inc. and contains confidential information that is not to
 * be reproduced or disclosed to any other person or entity without
 * prior written consent from Acer, Inc. in each and every instance.
 *
 * WARNING:  Unauthorized reproduction of this program as well as
 * unauthorized preparation of derivative works based upon the
 * program or distribution of copies by sale, rental, lease or
 * lending are violations of federal copyright laws and state trade
 * secret laws, punishable by civil and criminal penalties.
 *
 *****************************************************************************/

package com.example.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Shader.TileMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.Locale;

public class Wave {
    private static final String TAG = "Wave";
    private static final boolean DEBUG = true;
    public static final long WAVE_TIMER_DELAY = 650;
    /** Width of simulation */
    private int WIDTH = 1080;
    /** Resolution of simulation */
    private int NUM_POINTS;
    /** Spring constant for forces applied by adjacent points */
    private static final double SPRING_CONSTANT = 0.005d;
    /** Sprint constant for force applied to baseline */
    private static final double SPRING_CONSTANT_BASELINE = 0.005d;
    /** Vertical draw offset of simulation */
    private static final double Y_OFFSET = 0d;
    /** Damping to apply to speed changes */
    private static final double DAMPING = 0.99d;
    /**
     * Number of iterations of point-influences-point to do on wave per step
     * (this makes the waves animate faster)
     */
    private static final int ITERATIONS = 5;

    private static final int NUM_BACKGROUND_WAVES = 5;
    private static final int BACKGROUND_WAVE_MAX_HEIGHT = 1;
    private static final double BACKGROUND_WAVE_COMPRESSION = 1.0d / 160;

    /** A phase difference to apply to each sine */
    private int offset = 0;
    /** Amounts by which a particular sine is offset */
    private double sineOffsets[];
    /** Amounts by which a particular sine is amplified */
    private double sineAmplitudes[];
    /** Amounts by which a particular sine is stretched */
    private double sineStretches[];
    /** Amounts by which a particular sine's offset is multiplied */
    private double offsetStretches[];

    class WavePoint {
        double x;
        double y;
        double spd;
        double mass;

        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "x = %3d, y = %.2f, spd = %.2f, mass = %.2f"
                    , (int) x, y, spd, mass);
        }
    }

    private WavePoint mWavePoints[];
    private int mMaxHeight = 0;
    private int mCurrentHeight = 0;
    private Path mPath;
    private boolean mWaveInitFlag = false;
    // Paints
    Paint mBlodWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mTinyWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mWaterBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mUnderWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** screen width */
    private int mWidth;
    /** wave delta **/
    private int mWaveDelta = 0;
    private float mDensity = 1f;

    public Wave(Context context) {
        init(context);
    }

    /** unused */
    public Wave(Context context, int height, float delta) {
        init(context);
        setParams(height, delta);
    }

    public void setParams(int height, double delta) {
        if (DEBUG) Log.d(TAG, "height = " + height + " , delta = " + delta);

        mMaxHeight = height;
        WIDTH = (int) (mWidth + delta * 2.5f) + 2;
        mWaveDelta = (int) (delta / 2);
        offset = 0;
        NUM_POINTS = (int) (WIDTH / mDensity);
        makeWavePoint(NUM_POINTS);
        initPaint();
        initData();
    }

    private void makeWavePoint(int numPoints) {
        mWavePoints = new WavePoint[numPoints];

        for (int i = 0; i < numPoints; i++) {
            WavePoint pt = new WavePoint();
            // pt.x = (double) i / numPoints * WIDTH;
            pt.y = Y_OFFSET;
            pt.spd = 0;
            pt.mass = 1;
            mWavePoints[i] = pt;
        }
    }

    private void initWavePoint() {
        int numPoints = mWavePoints.length;

        for (int i = 0; i < numPoints; i++) {
            mWavePoints[i].x = (double) i / numPoints * WIDTH;
        }
    }

    private double usefloor(double min, double max) {
        return Math.random() * (max - min + 1) + min;
    }

    private void initData() {
        // Set each sine's values to a reasonable random value
        for (int i = 0; i < NUM_BACKGROUND_WAVES; i++) {
            sineOffsets[i] = -1 + 2 * usefloor(0.2d, 0.25d);
            sineAmplitudes[i] = usefloor(0.2d, 0.25d) * BACKGROUND_WAVE_MAX_HEIGHT;
            sineStretches[i] = usefloor(0.85d, 0.85d) * BACKGROUND_WAVE_COMPRESSION;
            offsetStretches[i] = usefloor(0.85d, 0.85d) * BACKGROUND_WAVE_COMPRESSION;
        }
        initWavePoint();
        mWaveInitFlag = true;
    }

    public void initPaint() {
        mTinyWavePaint.setStyle(Paint.Style.STROKE);
        mTinyWavePaint.setStrokeWidth(2);
        mTinyWavePaint.setColor(0x88FFFFFF);
        mWaterBorderPaint.setStyle(Paint.Style.STROKE);
        mWaterBorderPaint.setStrokeWidth(2);
        mWaterBorderPaint.setColor(0x44CFE5FF);
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 16 * mDensity, new int[] {
                0x88FFFFFF, 0x44CFE5FF, 0x0046596F
        }, new float[] {
                0f, 0.25f, 1f
        }, TileMode.CLAMP);
        LinearGradient linearGradient2 = new LinearGradient(0, 0, 0, mCurrentHeight * 0.35f, new int[] {
                0x00CFE5FF, 0x444a75a0, 0x44294058
        }, new float[] {
                0f, 45.7f * mDensity / mCurrentHeight, 1f
        }, TileMode.CLAMP);
        mBlodWavePaint.setShader(linearGradient);
        mUnderWavePaint.setShader(linearGradient2);
    }

    // init process
    private void init(Context context) {
        sineOffsets = new double[NUM_BACKGROUND_WAVES];
        sineAmplitudes = new double[NUM_BACKGROUND_WAVES];
        sineStretches = new double[NUM_BACKGROUND_WAVES];
        offsetStretches = new double[NUM_BACKGROUND_WAVES];
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mDensity = metrics.density;
        mPath = new Path();
    }

    /**
     * This function sums together the sines generated above, given an input
     * value x
     */
    private double overlapSines(int x) {
        double result = 0;

        for (int i = 0; i < NUM_BACKGROUND_WAVES; i++) {
            result += sineOffsets[i] + sineAmplitudes[i]
                    * Math.sin(x * sineStretches[i] + offset * offsetStretches[i]);
        }

        return result;
    }

    private void updateWavePoints(WavePoint[] points) {
        int numPoints;
        try {
            numPoints = points.length;
            for (int loop = 0; loop < ITERATIONS; loop++) {
                for (int n = 0; n < numPoints; n++) {
                    WavePoint p = points[n];

                    /** force to apply to this point */
                    double force = 0;
                    /**
                     * forces caused by the point immediately to the left or the
                     * right
                     */
                    double forceFromLeft, forceFromRight;

                    if (n == 0) { // wrap to left-to-right
                        forceFromLeft = SPRING_CONSTANT * (points[numPoints - 1].y - p.y);
                    } else {
                        forceFromLeft = SPRING_CONSTANT * (points[n - 1].y - p.y);
                    }

                    if (n == (numPoints - 1)) { // wrap to right-to-left
                        forceFromRight = SPRING_CONSTANT * (points[0].y - p.y);
                    } else {
                        forceFromRight = SPRING_CONSTANT * (points[n + 1].y - p.y);
                    }

                    // Also apply force toward the baseline
                    double forceToBaseline = SPRING_CONSTANT_BASELINE * (Y_OFFSET - p.y);

                    // Sum up forces
                    force += forceFromLeft + forceFromRight + forceToBaseline;

                    // Calculate acceleration
                    double acceleration = force / p.mass;

                    // Apply acceleration (with damping)
                    p.spd = DAMPING * p.spd + acceleration;

                    // Apply speed
                    p.y += p.spd;

                }
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "point is cleared");
            return;
        }

        synchronized (mPath) {
            mPath.rewind();
            mPath.moveTo(0, mMaxHeight);
            for (int n = 0; n < numPoints; n++) {
                WavePoint p = points[n];
                p.y += overlapSines((int) p.x);
                mPath.lineTo((float) p.x, (float) p.y);
            }
            mPath.lineTo(WIDTH, mMaxHeight);
        }
    }

    // update per heart beat
    public void update() {
        update(1);
    }

    public void update(int x) {
        if (mWavePoints == null || isWaveInit() == false) {
            Log.e(TAG, "mWavePoints is not ready");
            return;
        }
        offset += x;
        updateWavePoints(mWavePoints);
    }

    public void updateOffset(int offset) {
        this.offset = offset;
    }

    public void setWaveHeight(int currentHeight) {
        mCurrentHeight = currentHeight;
        initPaint();
    }

    // draw callback
    public void draw(Canvas canvas) {
        Path path;
        synchronized (mPath) {
            path = new Path(mPath);
        }
        canvas.drawPath(path, mTinyWavePaint);
        canvas.save();
        canvas.clipPath(path, Region.Op.INTERSECT);
        canvas.drawPaint(mBlodWavePaint);
        canvas.restore();
        canvas.save();
        path.offset(-mWaveDelta, 4 * mDensity);
        canvas.drawPath(path, mWaterBorderPaint);
        canvas.clipPath(path, Region.Op.INTERSECT);
        canvas.drawPaint(mUnderWavePaint);
        canvas.restore();
    }

    public boolean isWaveInit() {
        return mWaveInitFlag;
    }
}
