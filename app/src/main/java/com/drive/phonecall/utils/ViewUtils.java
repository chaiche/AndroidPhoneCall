package com.drive.phonecall.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class ViewUtils {

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 隱藏鍵盤
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager)
                context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(manager).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 導航列高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    /**
     * 狀態列高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
