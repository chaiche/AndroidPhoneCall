package com.drive.phonecall.task;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class PermissionTask {


    public PermissionTask(){

    }

    public String[] getPhonePermission(){
        String permissions[] = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            permissions = new String[]{READ_PHONE_STATE, CALL_PHONE, ANSWER_PHONE_CALLS};
        } else {
            permissions = new String[]{READ_PHONE_STATE, CALL_PHONE};
        }

        return permissions;
    }

    public boolean isPmGranted(Context context, String[] pms) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        boolean isGranted = true;
        for (String aPm : pms) {
            int pm = ActivityCompat.checkSelfPermission(context, aPm);
            if (pm != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
        }

        return isGranted;
    }

    public static void requestPm(Activity activity, String[] pms){

    }
}
