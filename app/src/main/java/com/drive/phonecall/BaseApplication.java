package com.drive.phonecall;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.drive.phonecall.data.SpData;
import com.drive.phonecall.utils.LanguageUtils;
import com.drive.phonecall.utils.SystemUtils;

import java.util.Locale;

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

        SpData spData = SpData.getInstance(getApplicationContext());

        String language = spData.getStringValue(SpData.LANGUAGE);
        if (TextUtils.isEmpty(language)) {
            language = LanguageUtils.getDefaultLanguage(this);
        }

        if (language.contains("en")) {
            LanguageUtils.setLanguage(this, "en");
        }
    }
}
