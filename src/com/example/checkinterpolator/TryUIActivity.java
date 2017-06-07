package com.example.checkinterpolator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tool.R;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TryUIActivity extends Activity {
    public static final String ACTION_FOR_SEARCHCITY = "com.acer.android.action.CITYSEARCH";
    TextView TV;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        TV.setText("Size : " + TV.getWidth() + "x" + TV.getHeight() + " px");
    }

    protected void dialog() {
        Date now = new Date();
//        String str = (String) DateUtils.getRelativeDateTimeString(
//                this, // Suppose you are in an activity or other Context subclass
//                now.getTime() - 86400 * 1000, // The time to display
//                DateUtils.MINUTE_IN_MILLIS, // The resolution. This will display only
//                // minutes (no "3 seconds ago")
//                DateUtils.MINUTE_IN_MILLIS, // The maximum resolution at which the time will switch
//                // to default date instead of spans. This will not
//                // display "3 weeks ago" but a full date instead
//                0); // Eventual flags

        String str = (String)DateUtils.getRelativeTimeSpanString(System.currentTimeMillis() - 60000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(str);
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout LL = (LinearLayout)View.inflate(this,R.layout.try_ui,null);
        setContentView(LL);

        final String rawSecondaryStorage = System.getenv("SECONDARY_STORAGE");
        LogHelper.d("path = " + rawSecondaryStorage);
        Log.d("LogHelper", "xxx");
        dialog();

//        final TextClock tc = (TextClock) findViewById(R.id.clock);
//        tc.setFormat12Hour(getString(R.string.home_timezone_12_hour));
//        tc.setTimeZone("GMT-8:00");
//        tc.setTextLocale(Locale.TAIWAN);
        String s = TimeZone.getTimeZone("Pacific/Honolulu").getDisplayName(false, TimeZone.LONG);

        // LogHelper.d();
        TV = (TextView) findViewById(R.id.tv);

        Shader textShader = new LinearGradient(0, 0, 20, 0, new int[]{
                Color.BLACK, Color.WHITE
        }, new float[]{
                0, 1
        }, TileMode.CLAMP);

        TV.getPaint().setShader(textShader);
        TV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TryUIActivity.this.sendBroadcast(new Intent("test.action"));
                Intent it = new Intent();
                it.setAction(ACTION_FOR_SEARCHCITY);
                startActivity(it);
            }
        });

        final Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.acer.android.weather.action.LOCALWEATHER");
//                intent.setPackage("com.google.android.apps.maps");
//                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                try {
//                    testBrowserPrivateDataAccess();
                } catch (Throwable e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        try {
            ActivityInfo[] list = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_ACTIVITIES).activities;

            for (int i = 0; i < list.length; i++) {
                System.out.println("List of running activities" + list[i].name);

            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setClassName("com.google.android.deskclock", "com.android.deskclock.DeskClock");
            List<ResolveInfo> infos = getPackageManager().queryIntentActivities(intent,
                    PackageManager.GET_RESOLVED_FILTER);

            for (ResolveInfo info : infos) {
                ActivityInfo activityInfo = info.activityInfo;
                IntentFilter filter = info.filter;
                // This activity resolves my Intent with the filter I'm looking
                // for
                String activityPackageName = activityInfo.packageName;
                String activityName = activityInfo.name;
                System.out.println("Activity " + activityPackageName + "/" + activityName);
            }
        } catch (NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        LL.addView(new WeatherStateTextView(this, "10:00", "hello..."));
    }

    /**
     * See Bug 6212665 for detailed information about this issue.
     */
    public void testBrowserPrivateDataAccess() throws Throwable {
        // Create a list of all intents for http display. This includes all browsers.
        List<Intent> intents = createAllIntents(Uri.parse("http://www.google.com"));
        String action = "\"" + "http://www.google.com/\"";
        // test each browser
        for (Intent intent : intents) {
            // define target file, which is supposedly protected from this app
            String targetFile = "file://" + getTargetFilePath();
            String html =
                    "<html><body>\n" +
                            "  <form name=\"myform\" action=" + action + " method=\"post\">\n" +
                            "  <input type='text' name='val'/>\n" +
                            "  <a href=\"javascript :submitform()\">Search</a></form>\n" +
                            "<script>\n" +
                            "  var client = new XMLHttpRequest();\n" +
                            "  client.open('GET', '" + targetFile + "');\n" +
                            "  client.onreadystatechange = function() {\n" +
                            "  if(client.readyState == 4) {\n" +
                            "    myform.val.value = client.responseText;\n" +
                            "    document.myform.submit(); \n" +
                            "  }}\n" +
                            "  client.send();\n" +
                            "</script></body></html>\n";
            String filename = "jsfileaccess.html";
            // create a local HTML to access protected file
            FileOutputStream out = openFileOutput(filename,
                    MODE_WORLD_READABLE);
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(html);
            writer.flush();
            writer.close();
            String filepath = getFileStreamPath(filename).getAbsolutePath();
            Uri uri = Uri.parse("file://" + filepath);
            // do a file request
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private String getTargetFilePath() throws Exception {
        FileOutputStream out = openFileOutput("target.txt", MODE_WORLD_READABLE);
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        writer.write("testing");
        writer.flush();
        writer.close();
        return getFileStreamPath("target.txt").getAbsolutePath();
    }

    /**
     * Create intents for all activities that can display the given URI.
     */
    private List<Intent> createAllIntents(Uri uri) {
        Intent implicit = new Intent(Intent.ACTION_VIEW);
        implicit.setData(uri);
        /* convert our implicit Intent into multiple explicit Intents */
        List<Intent> retval = new ArrayList<Intent>();
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(implicit, PackageManager.GET_META_DATA);
        for (ResolveInfo i : list) {
            Intent explicit = new Intent(Intent.ACTION_VIEW);
            explicit.setClassName(i.activityInfo.packageName, i.activityInfo.name);
            explicit.setData(uri);
            explicit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            retval.add(explicit);
        }
        return retval;
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    public static float spToPx(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    class WeatherStateTextView extends LinearLayout {

        public WeatherStateTextView(Context context, String time, String notification) {
            this(context, null, time, notification);
        }

        public WeatherStateTextView(Context context, @Nullable AttributeSet attrs, String time, String notification) {
            this(context, attrs, 0, time, notification);
        }

        public WeatherStateTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, String time, String notification) {
            this(context, attrs, defStyleAttr, 0, time, notification);
        }

        public WeatherStateTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, String time, String notification) {
            super(context, attrs, defStyleAttr, defStyleRes);
            setOrientation(LinearLayout.HORIZONTAL);
            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            setLayoutParams(p);
            addView(configText(context, Color.BLACK, 13, time));
            addView(configText(context, Color.BLACK, 13, notification));
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            TextView tv = (TextView)getChildAt(0);
            String s = (String)tv.getText();
        }

        private TextView configText(Context context, int color, float textSize, String text) {
            TextView tv = new TextView(context);
            LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, 150);
            p.setMarginEnd(100);
            tv.setLayoutParams(p);
            tv.setTextColor(color);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            tv.setText(text);
            return tv;
        }
    }
}
