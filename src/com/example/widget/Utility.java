package com.example.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utility {

    public static final String LAUNCHPAD_CLOSEFLOATAPP_ACTION = "com.acer.android.launchpad.closefloatapp";
    public static final String INTENT_KEY_PACKAGENAME = "packagename";
    public static final String INTENT_KEY_CLASSNAME = "classname";
    public static final String PACKAGENAME = "com.acer.android.floatapp2.floatspot";
    public static final String CLASSNAME = "com.acer.android.floatapp.floatspot.FloatSpotService";
    public static final String SETTINGS_PROVIDER_KEY = "com.acer.android.floatapp2.floatspot.float_buttons";

    public static Object invokeMethod(Method hideMethod, Object obj, Object... args) {
        Object o = null;

        try {
            o = hideMethod.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return o;
    }

    //invoke function
    public static Method getMethod(Object obj, String methodName, Class<?>... params) {
        Method hideMethod = null;
        try {
            hideMethod = obj.getClass().getMethod(methodName, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hideMethod;
    }

    //invoke static function
    public static Method getMethod(Class<?> classes, String methodName, Class<?>... params) {
        Method hideMethod = null;
        try {
            hideMethod = classes.getMethod(methodName, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hideMethod;
    }

    public static int[] getScreenSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return new int[]{
                size.x, size.y
        };
    }

    public static int[] getThreshold(Context ctx) {
        int sizeWidth = getScreenSize(ctx)[0];
        int sizeHeight = getScreenSize(ctx)[1];

        int threshold = 10;
        int thresholdLeft = threshold;
        int thresholdTop = threshold + getStatusBarHeight(ctx);
        int thresholdRight = sizeWidth - threshold;
        int thresholdBottom = sizeHeight - threshold;

        return new int[]{
                thresholdLeft, thresholdTop, thresholdRight, thresholdBottom
        };
    }

    public static int getStatusBarHeight(Context ctx) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = ctx.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static void saveButtonsStatus(Context ctx, int status, int orientation) {
        SharedPreferences settings = ctx.getSharedPreferences(SwitchButtonStatus.BUTTON_STATUS, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(SwitchButtonStatus.CHECK_BUTTON_STATUS, status);
        PE.putInt(SwitchButtonStatus.CHECK_ORIENTATION, orientation);
        PE.commit();
    }

    public static int loadButtonStatus(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(SwitchButtonStatus.BUTTON_STATUS, 0);
        return settings.getInt(SwitchButtonStatus.CHECK_BUTTON_STATUS, SwitchButtonStatus.FULL);
    }

    public static int loadLastOrientation(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(SwitchButtonStatus.BUTTON_STATUS, 0);
        return settings.getInt(SwitchButtonStatus.CHECK_ORIENTATION, LinearLayout.VERTICAL);
    }

    public static class SwitchButtonStatus {
        public static final String BUTTON_STATUS = "button.status";
        public static final String CHECK_BUTTON_STATUS = "check.button.status";
        public static final String CHECK_ORIENTATION = "orientation";

        public static final int DEFAULT = 1;
        public static final int FULL = 2;
    }

}
