
package com.example.checkinterpolator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class Utility {
    public static final String ACER_KEY = "7108487094be4e129b319ea7fd937981";
    
    public static final String _ID = "_id";
    public static final String CITIES_LOCALIZED = "localized";
    public static final String CITIES_COUNTRY_LOCALIZED = "clocalized";
    public static final String CITIES_ADMINISTRATIVE_LOCALIZED = "alocalized";
    public static final String CITIES_CONSTRAINT = "constraint";
    public static final String[] CITIES_PROJECTION = {
            _ID, CITIES_LOCALIZED, CITIES_COUNTRY_LOCALIZED, CITIES_ADMINISTRATIVE_LOCALIZED,
            CITIES_CONSTRAINT
    };

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else{
            State st = ni.getState();
            Log.v("LogManager","st = "+st);
            return true;
        }
            
    }

}
