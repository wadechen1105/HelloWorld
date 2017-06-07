package com.example.widget;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import com.example.checkinterpolator.LogHelper;

import java.lang.reflect.Method;

public class KeyButtonView extends ImageView {

    private static final long LONG_PRESS_TIMEOUT = 450;
    private long mDownTime;
    private int mCode;
    private int mTouchSlop;
    private boolean mSupportsLongpress = true;
    private boolean mIsLongPress;
    Runnable mCheckLongPress = new Runnable() {
        public void run() {
            if (isPressed()) {
                if (mCode != 0) {
                    sendEvent(KeyEvent.ACTION_DOWN, KeyEvent.FLAG_LONG_PRESS);
                    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
                } else {
                    // Just an old-fashioned ImageView
                    mIsLongPress = true;
                    performLongClick();
                }
            }
        }
    };

    public KeyButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setClickable(true);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    protected void setKeyCode(int code) {
        mCode = code;
    }

//    public boolean onTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//        int x, y;
//
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mDownTime = SystemClock.uptimeMillis();
//                setPressed(true);
//                if (mCode != 0) {
//                    sendEvent(KeyEvent.ACTION_DOWN, 0, mDownTime, mCode);
//                } else {
//                    // Provide the same haptic feedback that the system offers for virtual keys.
//                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                }
//                if (mSupportsLongpress) {
//                    removeCallbacks(mCheckLongPress);
//                    postDelayed(mCheckLongPress, LONG_PRESS_TIMEOUT);
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                x = (int) ev.getX();
//                y = (int) ev.getY();
//                setPressed(x >= -mTouchSlop && x < getWidth() + mTouchSlop && y >= -mTouchSlop
//                        && y < getHeight() + mTouchSlop);
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                setPressed(false);
//                if (mCode != 0) {
//                    sendEvent(KeyEvent.ACTION_UP, KeyEvent.FLAG_CANCELED);
//                }
//                if (mSupportsLongpress) {
//                    removeCallbacks(mCheckLongPress);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                final boolean doIt = isPressed();
//                setPressed(false);
//                if (mCode != 0) {
//                    if (doIt) {
//                        sendEvent(KeyEvent.ACTION_UP, 0);
//                        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
//                        // [ALPS00439010] We should NOT play sound here because PhoneWindow will play sound when it shows menu
//                        if (mCode != KeyEvent.KEYCODE_MENU) {
//                            playSoundEffect(SoundEffectConstants.CLICK);
//                        }
//                    } else {
//                        sendEvent(KeyEvent.ACTION_UP, KeyEvent.FLAG_CANCELED);
//                    }
//                } else {
//                    // no key code, just a regular ImageView
//                    if (doIt && !mIsLongPress) {
//                        performClick();
//                    }
//                }
//                if (mSupportsLongpress) {
//                    mIsLongPress = false;
//                    removeCallbacks(mCheckLongPress);
//                }
//                break;
//        }
//
//        return true;
//    }


    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        int x, y;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = SystemClock.uptimeMillis();
                setPressed(true);
                if (mCode != 0) {
                    sendEvent(KeyEvent.ACTION_DOWN, 0, mDownTime);
                } else {
                    // Provide the same haptic feedback that the system offers for virtual keys.
                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }
                if (mSupportsLongpress) {
                    removeCallbacks(mCheckLongPress);
                    postDelayed(mCheckLongPress, LONG_PRESS_TIMEOUT);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int)ev.getX();
                y = (int)ev.getY();
                setPressed(x >= -mTouchSlop && x < getWidth() + mTouchSlop && y >= -mTouchSlop
                        && y < getHeight() + mTouchSlop);
                break;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                if (mCode != 0) {
                    sendEvent(KeyEvent.ACTION_UP, KeyEvent.FLAG_CANCELED);
                }
                if (mSupportsLongpress) {
                    removeCallbacks(mCheckLongPress);
                }
                break;
            case MotionEvent.ACTION_UP:
                final boolean doIt = isPressed();
                setPressed(false);
                if (mCode != 0) {
                    if (doIt) {
                        sendEvent(KeyEvent.ACTION_UP, 0);
                        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                        // [ALPS00439010] We should NOT play sound here because PhoneWindow will play sound when it shows menu
                        if (mCode != KeyEvent.KEYCODE_MENU) {
                            playSoundEffect(SoundEffectConstants.CLICK);
                        }
                    } else {
                        sendEvent(KeyEvent.ACTION_UP, KeyEvent.FLAG_CANCELED);
                    }
                } else {
                    // no key code, just a regular ImageView
                    if (doIt) {
                        performClick();
                    }
                }
                if (mSupportsLongpress) {
                    removeCallbacks(mCheckLongPress);
                }
                break;
        }

        return true;
    }

    void sendEvent(int action, int flags) {
        sendEvent(action, flags, SystemClock.uptimeMillis());
    }

//    protected void sendEvent(int action, int flags, long when, int code) {
//        LogHelper.v("action = " + action + " , flags = " + flags
//                + ", when = " + when + " , mCode = " + code);
//        final int repeatCount = (flags & KeyEvent.FLAG_LONG_PRESS) != 0 ? 1 : 0;
//        int f = flags | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY;
//        final KeyEvent ev = new KeyEvent(mDownTime, when, action, code, repeatCount, 0, 0, 0, flags
//                | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
//                InputDevice.SOURCE_KEYBOARD);
//        InputManager.getInstance().injectInputEvent(ev, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
//    }

    void sendEvent(int action, int flags, long when) {
        LogHelper.v("sendEvent.. "+mCode);
        final int repeatCount = (flags & KeyEvent.FLAG_LONG_PRESS) != 0 ? 1 : 0;
        int f = flags | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY;
        final KeyEvent ev = new KeyEvent(mDownTime, when, action, mCode, repeatCount, 0, 0, 0,
                flags | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);
        Method method;
        android.hardware.input.InputManager inputManager;
        try {

            method = Utility.getMethod(android.hardware.input.InputManager.class, "getInstance");
            inputManager = (android.hardware.input.InputManager) Utility.invokeMethod(method, null);

            method = Utility.getMethod(inputManager, "injectInputEvent", InputEvent.class,
                    int.class);

            Utility.invokeMethod(method, inputManager, ev, 0);

        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e("Get exception : " + e);
        }
        //        InputManager.getInstance().injectInputEvent(ev, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
}
