
package com.example.tryl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.checkinterpolator.TryUIActivity;
import com.example.tool.R;

public class TrylActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_pad);

        final ImageButton ib = (ImageButton)findViewById(R.id.t1);
        
        ib.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.fubon.mbank.enterpriseuat", "com.fubon.mbank.enterpriseuat.MainActivity");
                startActivity(intent);
//com.fubon.mbank.android.permission.APP_PERMISSION
//                intent.setData(Uri.parse("market://details?id=com.acer.android.home"));
//                startActivity(intent);

            }
        });
    }
}
