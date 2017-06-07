
package com.example.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class RecentKey extends KeyButtonView {
    private static final int DEFAULT_RECENT_KEYCODE = KeyEvent.KEYCODE_MENU;
    private static final int ACER_RECENT_KEYCODE = 900;

    public RecentKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        setKeyCode(getKeyCode());
        super.onFinishInflate();
    }

    private int getKeyCode() {
        int keyCode = ACER_RECENT_KEYCODE;
        return keyCode;
    }

}
