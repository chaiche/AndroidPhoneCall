package com.drive.phonecall;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.drive.phonecall.data.SpData;
import com.drive.phonecall.utils.LanguageUtils;

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

        String language = SpData.getInstance(getApplicationContext())
                .getStringValue(SpData.LANGUAGE);
        if (TextUtils.isEmpty(language)) {
            language = LanguageUtils.getDefaultLanguage(this);
        }

        if (language.contains("en")) {
            LanguageUtils.setLanguage(this, "en");
        }
    }
}
