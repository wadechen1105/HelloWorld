package com.example.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

public class HomeKey extends KeyButtonView {
    Context mContext;

    public HomeKey(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    protected void onFinishInflate() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Activity) mContext, A.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                mContext.startActivity(intent);
            }
        });
        super.onFinishInflate();
    }

}
