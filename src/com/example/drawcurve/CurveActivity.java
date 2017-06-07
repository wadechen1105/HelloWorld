package com.example.drawcurve;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.acer.android.weatherlibrary.HourlyForecasts;
import com.acer.android.weatherlibrary.HourlyForecastsData;
import com.example.tool.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CurveActivity extends Activity {
    public static final String AUTHORITY = "com.acer.android.weather.provider.Weather";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + "hourlyforecasts");
    public static final String[] PROJECTION = new String[]{
            HourlyForecastsColumns._ID, // 0
            HourlyForecastsColumns.LOCATION_KEY,// 1
            HourlyForecastsColumns.DATETIME, // 2
            HourlyForecastsColumns.EPOCHDATETIME,// 3
            HourlyForecastsColumns.WEATHER_ICON, // 4
            HourlyForecastsColumns.ICONPHRASE,// 5
            HourlyForecastsColumns.TEMPERATUREVALUE,// 6
            HourlyForecastsColumns.TEMPERATUREUNIT,// 7
            HourlyForecastsColumns.PRECIPITATIONPROBABILITY,// 8
            HourlyForecastsColumns.MOBILELINK,// 9
            HourlyForecastsColumns.LINK,// 10
            HourlyForecastsColumns.DEVICEUPDATETIME
            // 11
    };
    boolean toogle = true;
    private BezierCurveView canvasView;
    private Button mBtnDrawPoints;

    synchronized public static HourlyForecasts getHourlyForecasts(Context context,
                                                                  String locationKey) {
        ContentResolver cr = context.getContentResolver();
        Uri qUri = Uri.withAppendedPath(CONTENT_URI, locationKey);
        Cursor cursor = cr.query(qUri, PROJECTION, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            HourlyForecasts hourlyForecasts = new HourlyForecasts();
            if (cursor.moveToFirst()) {
                do {
                    HourlyForecastsData hourlyForecastsData = new HourlyForecastsData();
                    hourlyForecastsData.LocationKey = cursor.getString(cursor
                            .getColumnIndex(HourlyForecastsColumns.LOCATION_KEY));
                    hourlyForecastsData.DateTime = cursor.getString(cursor
                            .getColumnIndex(HourlyForecastsColumns.DATETIME));
                    hourlyForecastsData.EpochDateTime = cursor.getLong(cursor
                            .getColumnIndex(HourlyForecastsColumns.EPOCHDATETIME));
                    hourlyForecastsData.WeatherIcon = cursor.getInt(cursor
                            .getColumnIndex(HourlyForecastsColumns.WEATHER_ICON));
                    hourlyForecastsData.IconPhrase = cursor.getString(cursor
                            .getColumnIndex(HourlyForecastsColumns.ICONPHRASE));
                    hourlyForecastsData.Temperature.Value = cursor.getDouble(cursor
                            .getColumnIndex(HourlyForecastsColumns.TEMPERATUREVALUE));
                    hourlyForecastsData.Temperature.Unit = cursor.getString(cursor
                            .getColumnIndex(HourlyForecastsColumns.TEMPERATUREUNIT));
                    hourlyForecastsData.PrecipitationProbability = cursor.getInt(cursor
                            .getColumnIndex(HourlyForecastsColumns.PRECIPITATIONPROBABILITY));
                    hourlyForecastsData.MobileLink = cursor.getString(cursor
                            .getColumnIndex(HourlyForecastsColumns.MOBILELINK));
                    hourlyForecastsData.Link = cursor.getString(cursor
                            .getColumnIndex(HourlyForecastsColumns.LINK));
                    hourlyForecastsData.DeviceUpdateTime = cursor.getLong(cursor
                            .getColumnIndex(HourlyForecastsColumns.DEVICEUPDATETIME));
                    hourlyForecasts.HourlyForecastLists.add(hourlyForecastsData);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return hourlyForecasts;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curve);
        getViews();
    }

    public int getMetricToImperialTempurature(double tempurature) {
        double result = (tempurature * (1.8)) + 32;
        return (int) result;
    }

    private List getHourlyForecastsList() {
        HourlyForecasts hourlyForecasts = getHourlyForecasts(this, "2518219");
        if (hourlyForecasts != null && hourlyForecasts.HourlyForecastLists.size() > 0) {
            ArrayList<Integer> tempuraturelist = new ArrayList<Integer>();
            ArrayList<Integer> rainlist = new ArrayList<Integer>();
            int tempurature = 0;
            for (int i = 0; i < hourlyForecasts.HourlyForecastLists.size(); i++) {
                double temp = hourlyForecasts.HourlyForecastLists.get(i).Temperature.Value;
                int rain = hourlyForecasts.HourlyForecastLists.get(i).PrecipitationProbability;

                Log.v("Tools", "temp = " + temp);
                tempurature = getMetricToImperialTempurature(temp);
                tempuraturelist.add(tempurature);
            }
            testGson(hourlyForecasts);
            return tempuraturelist;
        }
        return null;
    }

    class DTO {
        public Integer WeatherIcon = Integer.valueOf(0);
        public Boolean IsDaylight = Boolean.valueOf(false);
    }

    class Try {
        public Integer WeatherIcon = Integer.valueOf(0);
        public boolean IsDaylight;
        public DTO dto = new DTO();
        public String JustTest = "just-test";
        public HourlyForecasts h;
    }

    private void testGson(HourlyForecasts hourlyForecasts) {

        List<DTO> list = new ArrayList<>();
        for (HourlyForecastsData h : hourlyForecasts.HourlyForecastLists) {
            DTO dto = new DTO();
            dto.WeatherIcon = h.WeatherIcon;
            dto.IsDaylight = h.IsDaylight;
            list.add(dto);
        }


        Type type = new TypeToken<List<DTO>>() {
        }.getType();
        Gson g = new Gson();
        Try t = new Try();
        t.h = hourlyForecasts;
        String test = g.toJson(t);
//        String json = g.toJson(list, type);
        String json = gsonParser(list);
        Log.v("TestGson", "**Gson : " + json);
        Log.v("TestGson", "**test : " + test);
        t = g.fromJson(test,Try.class);
        for (HourlyForecastsData data:t.h.HourlyForecastLists)
            Log.v("TestGson", "**h : " + data.LocationKey);
    }

    private String gsonParser(List list){
        Type type = new TypeToken<List>() {
        }.getType();
        Gson g = new Gson();
        return g.toJson(list, type);
    }

    //For demo
    private ArrayList<Integer> mockData_t() {
        int[] t = {11, 11, 11, 11, 11, 11, 11, 12, 8, 5};
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : t) {
            list.add(i);
        }
        return list;
    }

    //For demo
    private ArrayList<Integer> mockData_r() {
        int[] t = {20, 17, 7, 8, 9, 80, 90, 10, 8, 0};
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : t) {
            list.add(i);
        }
        return list;
    }

    //For demo
    private ArrayList<Integer> mockData_t_same() {
        int[] t = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : t) {
            list.add((i * 2) + 30);
        }
        return list;
    }

    private void getViews() {
        Log.v("test-date","d = "+new Date(1455829200000L));
        final List hList = getHourlyForecastsList();
        canvasView = (BezierCurveView) findViewById(R.id.curve_view);
        final TemperatureCurveView tView = (TemperatureCurveView) findViewById(R.id.t_curve_view);

        mBtnDrawPoints = (Button) findViewById(R.id.draw);
        mBtnDrawPoints.setText("draw curve");
        mBtnDrawPoints.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mockData_t() != null) {
                    if (toogle) {
//                        canvasView.drawCurve(mockData_r());
                        tView.drawCurve(mockData_t());
                    } else {
//                        canvasView.drawCurve(new ArrayList<Integer>());
                        tView.drawCurve(new ArrayList<Integer>());
                    }
                    toogle = !toogle;
                } else {
                    Snackbar.make(mBtnDrawPoints, "NO DATA", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * HourlyForecasts-Columns
     */
    public static final class HourlyForecastsColumns implements BaseColumns {
        public static final String TABLE_NAME = "hourlyforecasts";

        /**
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String LOCATION_KEY = "location_key";
        /**
         * <p/>
         * Type: VARCHAR
         * </P>
         */
        public static final String DATETIME = "datetime";
        /**
         * <p/>
         * Type: BIGINT
         * </P>
         */
        public static final String EPOCHDATETIME = "epochDatetime";
        /**
         * <p/>
         * Type: INTEGER
         * </P>
         */
        public static final String WEATHER_ICON = "weatherIcon";
        /**
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String ICONPHRASE = "iconPhrase";
        /**
         * <p/>
         * Type: FLOAT
         * </P>
         */
        public static final String TEMPERATUREVALUE = "tempurature_value";
        /**
         * <p/>
         * Type: VARCHAR
         * </P>
         */
        public static final String TEMPERATUREUNIT = "tempurature_unit";
        /**
         * <p/>
         * Type: INTEGER
         * </P>
         */
        public static final String PRECIPITATIONPROBABILITY = "precipitationProbability";
        /**
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String MOBILELINK = "mobilelink";
        /**
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String LINK = "link";
        /**
         * <p/>
         * Type: BIGINT
         * </P>
         */
        public static final String DEVICEUPDATETIME = "deviceUpdatetime";
    }
}
