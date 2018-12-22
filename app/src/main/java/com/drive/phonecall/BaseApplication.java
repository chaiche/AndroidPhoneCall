package com.drive.phonecall;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

@SuppressWarnings("unused")
public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
