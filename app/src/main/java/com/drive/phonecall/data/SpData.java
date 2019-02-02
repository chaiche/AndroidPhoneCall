package com.drive.phonecall.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SpData {

    private static SpData mInstance;

    private Context mContext;

    private static final String APP = "app";
    public static final String LANGUAGE = "FIRST_OPEN";

    private SpData(Context context){
        this.mContext = context;
    }

    public static synchronized SpData getInstance(Context context){
        if(mInstance == null) {
            mInstance = new SpData(context);
        }

        return mInstance;
    }

    private SharedPreferences getSharePreferences(){
        return mContext.getSharedPreferences(APP, Context.MODE_PRIVATE);
    }

    public String getStringValue(String key){
        return getSharePreferences().getString(key, "");
    }

    public void putStringValue(String key, String s){
        getSharePreferences().edit().putString(key, s).apply();
    }
}
