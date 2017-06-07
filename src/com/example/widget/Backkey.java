
package com.example.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class Backkey extends KeyButtonView {

    public Backkey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        setKeyCode(KeyEvent.KEYCODE_BACK);
        super.onFinishInflate();
    }

}
