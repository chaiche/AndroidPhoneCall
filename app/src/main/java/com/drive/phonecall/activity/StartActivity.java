package com.drive.phonecall.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import com.drive.phonecall.BaseActivity;
import com.drive.phonecall.R;
import com.drive.phonecall.data.SpData;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class StartActivity extends BaseActivity{

    @Override
    protected void initial(Bundle savedInstanceState) {

    }

    @Override
    protected View onCreateView() {
        return View.inflate(this, R.layout.activity_start, null);
    }

    @Override
    public void onResume(){
        super.onResume();

        boolean first = SpData.getInstance(this).getBooleanValue(SpData.FIRST_OPEN);
        if(first){
            checkPermission();
        }
    }

    private void checkPermission(){
        if (!checkPermissions()) {
            return;
        }

        if (!checkOverlay()) {
            return;
        }

        if (!checkNotification()) {
            return;
        }
    }

    public Boolean checkPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {

            String permissions[] = new String[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                permissions = new String[]{READ_PHONE_STATE, CALL_PHONE, ANSWER_PHONE_CALLS};
            } else {
                permissions = new String[]{READ_PHONE_STATE, CALL_PHONE};
            }

            List<String> pm_list = new ArrayList<>();
            for (String permission : permissions) {
                int pm = ActivityCompat.checkSelfPermission(this, permission);
                if (pm != PackageManager.PERMISSION_GRANTED) {
                    pm_list.add(permission);
                }
            }
            if (pm_list.size() > 0) {
                ActivityCompat.requestPermissions(this, pm_list.toArray(new String[pm_list.size()]), 1);
                return false;
            }
        }
        return true;
    }

    @SuppressLint("InlinedApi")
    public boolean checkOverlay() {
        boolean isEnable = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
        if (!isEnable) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(String.format("package:%s", getPackageName())));
            startActivity(intent);
        }
        return isEnable;
    }

    public boolean checkNotification() {
        boolean isEnable = NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
        if (!isEnable) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
        return isEnable;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            checkPermissions();
                            return;
                        }
                    }
                }
        }
    }
}
