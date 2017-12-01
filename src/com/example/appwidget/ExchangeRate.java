package com.example.appwidget;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * DTO for ExchangeRate info
 **/
public class ExchangeRate {

    private ArrayList<CurrencyType> mCurrencyList;
    private String mSystemId;
    private int mStatusCode;
    private String mStatusDesc;
    private String mErrorDisplay;
    private String mClientTime;
    private String mToken;
    private String mUpdateTime;
    private String mUnit;

    public ArrayList<CurrencyType> getCurrencyList() {
        return mCurrencyList;
    }

    public String getSystemId() {
        return mSystemId;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getStatusDesc() {
        return mStatusDesc;
    }

    public String getErrorDisplay() {
        return mErrorDisplay;
    }

    public String getClientTime() {
        return mClientTime;
    }

    public String getToken() {
        return mToken;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public String getUnit() {
        return mUnit;
    }

    public static ExchangeRate fromJsonString(String json) {
        ExchangeRate detailData = null;

        if (json == null) return null;

        try {
            detailData = new ExchangeRate();

            JSONObject rootJObj = new JSONObject(json);
            detailData.mSystemId = rootJObj.getString("SystemId");
            detailData.mStatusCode = rootJObj.getInt("StatusCode");
            detailData.mStatusDesc = rootJObj.getString("RsData");
            detailData.mErrorDisplay = rootJObj.getString("ErrorDisplay");
            detailData.mClientTime = rootJObj.getString("ClientTime");
            detailData.mToken = rootJObj.getString("Token");
            detailData.mErrorDisplay = rootJObj.getString("ErrorDisplay");
            JSONObject rsData = rootJObj.getJSONObject("RsData");
            detailData.mUpdateTime = rsData.getString("UpdateTime");
            detailData.mUnit = rsData.getString("Unit");

            L.d(rsData.toString());
            String jsonArr = rsData.getJSONArray("LongTermRateInfos").toString();
            Type listType = new TypeToken<ArrayList<CurrencyType>>() {
            }.getType();
            Gson gson = new Gson();
            detailData.mCurrencyList = gson.fromJson(jsonArr, listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detailData;
    }

    public static class CurrencyType {
        //Currency code
        @SerializedName("CurrencyCode1")
        private String mCurrencyCode1;

        //Currency name
        @SerializedName("CurrencyName1")
        private String mCurrencyName1;

        //Buy value
        @SerializedName("Buy")
        private String mBuy;

        //Sell Value
        @SerializedName("Sell")
        private String mSell;

        public String getCurrencyCode1() {
            return mCurrencyCode1;
        }

        public String getCurrencyName1() {
            return mCurrencyName1;
        }

        public String getBuyValue() {
            return mBuy;
        }

        public String getSellValue() {
            return mSell;
        }
    }


}