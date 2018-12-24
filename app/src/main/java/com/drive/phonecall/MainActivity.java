package com.drive.phonecall;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.drive.phonecall.call.CallService;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {

    private CallService mService;
    private LoaclServiceConnection mLoaclServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();

        check();
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    private void check() {

        if (!checkPermissions()) {
            return;
        }

        if (!checkOverlay()) {
            return;
        }

        if (!checkNotification()) {
            return;
        }

        init();
    }

    private void init() {
        mLoaclServiceConnection = new LoaclServiceConnection();
        bindService(new Intent(MainActivity.this, CallService.class), mLoaclServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private class LoaclServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((CallService.CallServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mService != null) {
            unbindService(mLoaclServiceConnection);
        }
    }

    public void startPhoneListener(View view) {
        if (mService != null) {
            mService.startPhoneListener();
        }
    }

    public void stopPhoneListener(View view) {
        if (mService != null) {
            mService.stopPhoneListener();
        }
    }

    public void startLineListener(View view) {
        if (mService != null) {
            mService.startLineListener();
        }
    }

    public void stopLineListener(View view) {
        if (mService != null) {
            mService.stopLineListener();
        }
    }
}
