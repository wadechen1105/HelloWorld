
package com.example.checkinterpolator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tool.R;

public class CheckinterpolatorActivity extends AppCompatActivity {

    private CheckinterpolatorView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interpolator_view);
        mView = (CheckinterpolatorView)findViewById(R.id.view);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < mView.OBJ_COUNT; i++) {
            menu.add(0, i, Menu.NONE, mView.getInterpolatorName(i));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getSupportActionBar().setTitle(item.getTitle());
        mView.drawCurve(item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}
