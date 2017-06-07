/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.webviewtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.tool.R;


/**
 * Sample creating 1 webviews.
 */
public class WebView1 extends Activity {
    private static final String TAG = "LogHelper";
    private ProgressDialog progressBar;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        LayoutParams wmParams = new LayoutParams();
//
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        wmParams.format = PixelFormat.RGBA_8888;
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
//
//        wmParams.width = 1200;
//        wmParams.height = 1200;
//
//
        View v = View.inflate(this, R.layout.webview_1, null);
//        wm.addView(v, wmParams);
        setContentView(v);

        WebView webview = (WebView) v.findViewById(R.id.wv1);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setGeolocationEnabled(true);

        // open icon db to display the Android WebView's favicon
        String faviconPtah = getDir("icons", Context.MODE_PRIVATE).getPath();
        WebIconDatabase.getInstance().open(faviconPtah);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setDomStorageEnabled(true);

        // Below required for geolocation
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationDatabasePath(getDir("geolocation", 0).getPath());
        settings.setGeolocationEnabled(true);

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
            }
        });


        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "url: " + url);
                view.loadUrl(url);
                return true;
            }
        });

        String data = "<html>\n" +
                "<head>\n" +
                "<title>Test...</title>" +
                "    <link rel=\"alternate\"\n" +
                "          href=\"android-app://tools\" />\n" +
                "</head>\n" +
                "<body><b>Hello, world!</b>" +
                "</body>";

        //"http://goo.gl/Y7Hh6N"
        //"https://maps.google.com.tw"
//        webview.loadUrl("https://maps.google.com.tw");
        webview.loadData(data, "text/html", "UTF-8");
    }
}
