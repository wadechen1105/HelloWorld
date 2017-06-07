package com.example.tryl;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.tool.R;

public class TrylActivityTest extends ActivityInstrumentationTestCase2<TrylActivity> {
    private TrylActivity mTrylActivity;
    private ImageButton mXXXButton;

    public TrylActivityTest() {
        super(TrylActivity.class);
    }

    public void setUp() throws Exception {
        setActivityInitialTouchMode(true);
        mTrylActivity = getActivity();
        mXXXButton = (ImageButton) mTrylActivity.findViewById(R.id.t1);
    }

    public void tearDown() throws Exception {

    }

    public void testXXXButton_layout() {
        final View decorView = mTrylActivity.getWindow().getDecorView();
        assertNotNull(mXXXButton);
        ViewAsserts.assertOnScreen(decorView, mXXXButton);

        final ViewGroup.LayoutParams layoutParams = mXXXButton.getLayoutParams();
        assertNotNull(layoutParams);
        TouchUtils.clickView(this, mXXXButton);
    }
}
