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

package com.example.checkinterpolator;

import android.util.Log;

public class LogHelper {

    private static final String TAG = "LogHelper";

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    private static boolean LOG_FLAG = true;

    static private void out(int LEVEL, String fmt, Object... args) {
        // if debug == false
        if (!LOG_FLAG)
            return;

        if (fmt == null) {
            fmt = "";
        } else {
            fmt = " -- Info : " + fmt;
        }
        String s0 = String.format(fmt, args);
        StackTraceElement st = new RuntimeException().getStackTrace()[2];
        String className = st.getClassName();
        String s = "[ " + className.substring(className.lastIndexOf(".") + 1) + " , Line : "
                + st.getLineNumber() + "]" + s0;

        switch (LEVEL) {
            case VERBOSE:
                Log.v(TAG, s);
                return;
            case DEBUG:
                Log.d(TAG, s);
                return;
            case INFO:
                Log.i(TAG, s);
                return;
            case WARN:
                Log.w(TAG, s);
                return;
            case ERROR:
                Log.e(TAG, s);
                return;
            case ASSERT:
                Log.wtf(TAG, s);
                return;
        }
    }

    private static int lastIndexOf(String string) {
        // TODO Auto-generated method stub
        return 0;
    }

    static public void v(String fmt, Object... args) {
        out(VERBOSE, fmt, args);
    }

    static public void d(String fmt, Object... args) {
        out(DEBUG, fmt, args);
    }

    static public void i(String fmt, Object... args) {
        out(INFO, fmt, args);
    }

    static public void w(String fmt, Object... args) {
        out(WARN, fmt, args);
    }

    static public void e(String fmt, Object... args) {
        out(ERROR, fmt, args);
    }

    static public void wtf(String fmt, Object... args) {
        out(ASSERT, fmt, args);
    }

    static public void printStack(int length) {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ((length > ste.length) ? ste.length : length); i++) {
            Log.i(TAG, "<" + i + "> :" + ste[i]);
        }
    }
}
