package com.drive.phonecall.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.drive.phonecall.R;
import com.drive.phonecall.call.CallService;
import com.drive.phonecall.utils.SystemUtils;

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
