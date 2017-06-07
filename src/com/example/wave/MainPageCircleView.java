/*****************************************************************************
 * Copyright (C) 2011 Acer, Inc. All Rights Reserved.
 * <p/>
 * This program is an unpublished copyrighted work which is proprietary
 * to Acer, Inc. and contains confidential information that is not to
 * be reproduced or disclosed to any other person or entity without
 * prior written consent from Acer, Inc. in each and every instance.
 * <p/>
 * WARNING:  Unauthorized reproduction of this program as well as
 * unauthorized preparation of derivative works based upon the
 * program or distribution of copies by sale, rental, lease or
 * lending are violations of federal copyright laws and state trade
 * secret laws, punishable by civil and criminal penalties.
 *****************************************************************************/

package com.example.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.example.tool.R;

import java.util.List;

public class MainPageCircleView extends LinearLayout {
    private static final String TAG = "CareCenter.MainPageCircleView";
    private static final boolean DEBUG = false;

    /** slow down the animation */
    private static final long DEBUG_ANIMATION = 5;
    private static final long WAVE_ANIMATION_DURATION = 250 * DEBUG_ANIMATION;
    Context mContext;
    private int mRotation;
    private Wave mWave;
    private HandlerThread mWaveHandlerThread;
    private Handler mWaveRedrawHandler;
    private Rect mCurrentMaxWaterRect = new Rect();
    private int mWaveHeight;
    private int mWaveMaxHeight = 400;
    private double mDrawDelta = 0;
    private int mMaxDegree = 15;
    private int lastRenderDegree = 0;
    private int mCurrentOrientation;
    private Runnable mWaveRedrawRunnable = new Runnable() {
        int lower = 9;
        int upper = 12;

        @Override
        public void run() {
            if (mWave == null) {
                return;
            }
            synchronized (mWave) {
                int orient = mCurrentOrientation;
                if ((360 - mCurrentOrientation) < lower || mCurrentOrientation < lower) {
                    orient = lower;
                } else if ((mCurrentOrientation > upper && mCurrentOrientation <= mMaxDegree)
                        || (mCurrentOrientation < 360 - upper && mCurrentOrientation >= 360 - mMaxDegree)) {
                    orient = upper;
                }
                mWave.update((int) (getWidth() / 5 * Math.tan(Math.toRadians(orient))));
                if (DEBUG) {
                    Log.d(TAG, "mCurrentOrientation " + mCurrentOrientation + " orient " + orient);
                }
                Rect r = mCurrentMaxWaterRect;
                postInvalidate(r.left, r.top, r.right, r.bottom);
                mWaveRedrawHandler.removeCallbacks(mWaveRedrawRunnable);
                mWaveRedrawHandler.postDelayed(mWaveRedrawRunnable, Wave.WAVE_TIMER_DELAY);
            }
        }
    };
    private OrientationEventListener mOrientationEventListener;
    private TranslateAnimation mWaterInAnimation;
    private TranslateAnimation mWaterOutAnimation;
    private Runnable mWaterOutRunnable;
    private int mSurfaceDif = 40;

    public MainPageCircleView(Context context) {
        super(context);
        mContext = context;
        initUI();
    }

    public MainPageCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setWillNotDraw(false);
        initUI();
    }
    public MainPageCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initUI();
    }

    private void startWave() {
        stopWave();
        mWaveHandlerThread = new HandlerThread("wave");
        mWaveHandlerThread.start();
        mWaveRedrawHandler = new Handler(mWaveHandlerThread.getLooper());
        mWaveRedrawHandler.post(mWaveRedrawRunnable);
    }

    private void stopWave() {
        if (mWaveHandlerThread != null) {
            mWaveHandlerThread.getLooper().quit();
            try {
                mWaveHandlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWaveHandlerThread = null;
        }
        if (mWaveRedrawHandler != null) {
            mWaveRedrawHandler.removeCallbacks(mWaveRedrawRunnable);
        }
    }

    public void animateViewIn(int percentage) {
        if (mWave == null) {
            Log.e(TAG, "Wave is null");
            return;
        }
        cleanAnimation(mWaterInAnimation);

        mWave.updateOffset(0);
        mWaveHeight = mWaveMaxHeight * percentage / 100;
        mWave.setWaveHeight(mWaveHeight);
        mWaterInAnimation = new TranslateAnimation(0, 0, mWaveHeight, 0);
        mWaterInAnimation.setInterpolator(new AccelerateInterpolator(1.5f));
        mWaterInAnimation.setDuration(WAVE_ANIMATION_DURATION);
        mWaterInAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                stopWave();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(VISIBLE);
                startWave();
            }
        });
        startAnimation(mWaterInAnimation);
    }

    public void animateViewOut() {
        if (mWave == null) {
            Log.e(TAG, "Wave is null");
            return;
        }
        cleanAnimation(mWaterOutAnimation);
        mWaterOutAnimation = new TranslateAnimation(0, 0, 0, mWaveHeight);
        mWaterOutAnimation.setDuration(WAVE_ANIMATION_DURATION);
        mWaterOutAnimation.setInterpolator(new AccelerateInterpolator());
        mWaterOutAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                stopWave();
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        startAnimation(mWaterOutAnimation);
    }

    private void cleanAnimation(Animation animation) {
        if (animation == null) {
            return;
        }
        if (animation instanceof AnimationSet) {
            AnimationSet animationSet = (AnimationSet) animation;
            List<Animation> animations = animationSet.getAnimations();
            for (Animation ani : animations) {
                cleanAnimation(ani);
            }
        }
        animation.reset();
        animation.setAnimationListener(null);
    }

    private void initUI() {
        mMaxDegree = getResources().getInteger(R.integer.max_degree);
        mSurfaceDif = getResources().getDimensionPixelSize(R.dimen.water_surface_diff);

        mRotation = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();

        mOrientationEventListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    orientation = 0;
                }

                int max = mMaxDegree;
                int newOrientation;

                //transform to correct angle for current rotation
                if (mRotation == Surface.ROTATION_90) {
                    newOrientation = (orientation + 90) % 360;
                } else if (mRotation == Surface.ROTATION_180) {
                    newOrientation = (orientation + 180) % 360;
                } else if (mRotation == Surface.ROTATION_270) {
                    newOrientation = (orientation + 270) % 360;
                } else {
                    newOrientation = orientation;
                }

                if (newOrientation > max && newOrientation <= 180) {
                    newOrientation = max;
                } else if (newOrientation > 180 && newOrientation <= (360 - max)) {
                    newOrientation = 360 - max;
                }
                if (Math.abs(mCurrentOrientation - newOrientation) > 0) {
                    if (DEBUG) {
                        Log.d(TAG, "orientation " + orientation + " -> " + newOrientation);
                    }
                    synchronized (mWave) {
                        int offset = 100000 / ((Math.abs(mCurrentOrientation - newOrientation) ^ 3) + 1);
                        mWave.updateOffset(offset);
                    }
                    mCurrentOrientation = newOrientation;
                }
            }
        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        mWave = new Wave(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
            mOrientationEventListener = null;
        }
        stopWave();
        mWave = null;
        cleanAnimation(mWaterInAnimation);
        cleanAnimation(mWaterOutAnimation);
        mWaterInAnimation = null;
        mWaterOutAnimation = null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!changed) {
            return;
        } else if (mWave == null) {
            return; //for low end device
        }

        calcRect(mMaxDegree, mCurrentMaxWaterRect);
        mWaveMaxHeight = getHeight() * 19 / 20;
        calcDrawWidth();
        mWave.setParams(getHeight(), 2 * mDrawDelta);

        startWave();
    }

    private void calcRect(int d, Rect outRect) {
        int width = getWidth();
        int height = getHeight();
        int waterHeight = mWaveHeight;
        double degree = d;

        double delta = Math.tan(Math.toRadians(degree)) * width / 2f + 20;
        outRect.left = 0;
        outRect.right = width;
        outRect.top = 0;
        outRect.bottom = height;
    }

    /** calculate overdraw delta */
    private void calcDrawWidth() {
        int width = getWidth();
        double degree = Math.toRadians(mMaxDegree);
        // have to draw more ?? to show after rotate back (inside the screen)
        // (1 / cosθ) * (w / 2) - w / 2
        mDrawDelta = (1 / Math.cos(degree) - 1) * width / 2f;
        // calculate the triangle outside the screen for water
        // ( h - tanθ * w /2) * sinθ
        double drawDeltaForWater = (mWaveMaxHeight - Math.tan(degree) * width / 2f)
                * Math.sin(degree);
        mDrawDelta += drawDeltaForWater;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWave == null) {
            return; //for low end device
        }
        int width = getWidth();
        int height = getHeight();
        int max = mWaveHeight;
        float dx = 0, dy = 0;
        dx = width / 2;
        dy = height - max;

        canvas.save();
        if (mCurrentOrientation < 180) {
            lastRenderDegree += Math.ceil((mCurrentOrientation - lastRenderDegree) * 0.30f);
        } else {
            lastRenderDegree += Math.ceil((mCurrentOrientation - 360 - lastRenderDegree) * 0.30f);
        }
        if (lastRenderDegree > max) {
            lastRenderDegree = max;
        } else if (lastRenderDegree < -max) {
            lastRenderDegree = -max;
        }
        canvas.rotate(-lastRenderDegree, dx, dy);
        canvas.translate((float) -mDrawDelta, dy + (float) (mSurfaceDif * Math.abs(Math.sin(Math.toRadians(lastRenderDegree)))));

        mWave.draw(canvas);

        canvas.restore();
    }

    public void finishThread() {
        if (mWaveHandlerThread != null) {
            mWaveHandlerThread.quit();
            mWaveHandlerThread = null;
        }
    }

    public void cancelAnimations() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
            mOrientationEventListener = null;
        }
        stopWave();
        mWave = null;
        cleanAnimation(mWaterInAnimation);
        cleanAnimation(mWaterOutAnimation);
        mWaterInAnimation = null;
        mWaterOutAnimation = null;
    }
}