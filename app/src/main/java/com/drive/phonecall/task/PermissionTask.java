package com.drive.phonecall.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class PermissionTask {

    private Context mContext;

    public PermissionTask(Context context) {
        this.mContext = context;
    }

    public boolean isPhonePmGranted(){
        return isPmGranted(getPhonePermission());
    }

    public boolean isAllPmGranted(Context context){
        return isPhonePmGranted() && isOverLayGranted(context) && isNotificationGranted();
    }

    public String[] getPhonePermission() {
        String permissions[] = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            permissions = new String[]{READ_PHONE_STATE, CALL_PHONE, ANSWER_PHONE_CALLS};
        } else {
            permissions = new String[]{READ_PHONE_STATE, CALL_PHONE};
        }

        return permissions;
    }

    public boolean isPmGranted(String[] pms) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        boolean isGranted = true;
        for (String aPm : pms) {
            int pm = ActivityCompat.checkSelfPermission(mContext, aPm);
            if (pm != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
        }

        return isGranted;
    }

    public boolean isOverLayGranted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public void requestOverLayPm() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse(String.format("package:%s", mContext.getPackageName())));
        mContext.startActivity(intent);
    }

    public boolean isNotificationGranted() {
        return NotificationManagerCompat.getEnabledListenerPackages(mContext).contains(mContext.getPackageName());
    }

    public void requestNotificationPm(){
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        mContext.startActivity(intent);
    }
}
