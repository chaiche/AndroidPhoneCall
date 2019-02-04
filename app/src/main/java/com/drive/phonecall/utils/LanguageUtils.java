package com.drive.phonecall.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageUtils {

    public static final String ACTION_CHANGE_LANGUAGE = "ACTION_CHANGE_LANGUAGE";

    public static void setLanguage(Context c, String language) {
        setNewLocale(c, language);
    }

    public static void setNewLocale(Context c, String language) {
        updateResources(c, language);
    }

    public static String getDefaultLanguage(Context context){
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }

        return locale.getLanguage();
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
