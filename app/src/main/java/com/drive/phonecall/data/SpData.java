package com.drive.phonecall.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SpData {

    private static SpData mInstance;

    private Context mContext;

    private static final String APP = "app";
    public static final String FIRST_OPEN = "FIRST_OPEN";

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

    public boolean getBooleanValue(String key){
        return getSharePreferences().getBoolean(key, false);
    }

    public void putBooleanValue(String key, boolean b){
        getSharePreferences().edit().putBoolean(key, b).apply();
    }
}
