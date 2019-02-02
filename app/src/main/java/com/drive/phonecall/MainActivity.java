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
import android.util.Log;
import android.view.View;

import com.drive.phonecall.call.CallService;
import com.drive.phonecall.service.NotificationReceiver;
import com.drive.phonecall.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();

        init();
    }

    private void init() {
        Intent it = new Intent(MainActivity.this, CallService.class);

        boolean isRunning = SystemUtils.isServiceRunning(this, CallService.class);
        if(!isRunning) {
            startService(it);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
