package com.example.checkinterpolator;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.SimpleAdapter;

import com.example.tool.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckDeviceInfomationActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_1,
                new String[]{
                        "title"
                }, new int[]{
                android.R.id.text1
        }));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        DeviceInfo device = new DeviceInfo();

        List<String> list = device.getInfo();
        int len = list.size();
        Log.v("LogManager", "[len] = " + len);

        for (int i = 0; i < len; i++) {
            String info = list.get(i);

            addItem(myData, info);
        }

        return myData;
    }

    protected void addItem(List<Map<String, Object>> data, String name) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        data.add(temp);
    }

    class DeviceInfo {
        int width;
        int height;
        float rate;
        int densityDpi;

        public DeviceInfo() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            densityDpi = (int) (metrics.density * 160f);
            rate = densityDpi / 160.0f;
        }

        List<String> getInfo() {
            List<String> list = new ArrayList<String>();
            list.add(modelName());
            list.add(dpi());
            list.add(screenSize());
            list.add(settingLanguage());
            list.add(valuefolderName());
            list.add(getStatusBarHeight());
            list.add(showAllActionNamePackage("com.acer.android.pip2.PICK_PIPPLUGIN"));
            return list;
        }

        private String screenSize() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            width = size.x;
            height = size.y;
            int wDp = (int) (width / rate);
            int hDp = (int) (height / rate);

            String str = "Screen width = " + width + " px , height = " + height + " px, w-dp = " + wDp
                    + " , h-dp = " + hDp;

            return str;
        }

        private String modelName() {
            return "Model Name : " + android.os.Build.MODEL + "\n" +
                    "SDK : " + Build.VERSION.SDK_INT;

            /*
            * //sdk verson
String sdkName = Build.VERSION.RELEASE;
String sdkVerson = Build.VERSION.SDK;
System.out.println("sdkName = "+sdkName+" || sdkVersion = "+sdkVerson);
//sdkName = 2.1-update_1 || sdkVersion = 7


//app verson
try {
String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
System.out.println("name = "+versionName+" || code = "+versionCode);
} catch (NameNotFoundException e) {
//Handle exception
}*/
        }

        private String dpi() {
            return "Density Dpi =  " + densityDpi + " , Rate = [" + rate + "]";
        }

        private String settingLanguage() {
            String la = Locale.getDefault().getDisplayLanguage();
            return "Language : " + la;
        }

        private String valuefolderName() {
            return "Folder Name : " + getString(R.string.f_name);
        }

        private String getStatusBarHeight() {
            int result = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
            float r = result / rate;
            return "StatusBarHeight (dp): " + r;
        }

        private String showAllActionNamePackage(String action) {
            PackageManager packageManager = getPackageManager();
            Intent baseIntent = new Intent(action);
            baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
            List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
                    PackageManager.GET_RESOLVED_FILTER);
            StringBuilder sb = new StringBuilder("Result:\n");
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo info = list.get(i);
                ServiceInfo sinfo = info.serviceInfo;
                sb.append(sinfo.packageName + " / " + sinfo.name + "\n");
                LogHelper.v(sinfo.packageName + " / " + sinfo.name + "\n");
            }
            return sb.toString();
        }
    }
}
