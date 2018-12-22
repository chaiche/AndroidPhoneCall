package com.drive.phonecall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.simple.SimpleActivity;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private OverlayView mOverlayView;
    private CallManager mCallManager;

    private TextView mTxvState;
    private Button mBtnAcceptCall;
    private Button mBtnRejectCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check();
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
        mCallManager = new CallManager(this);
        createView();

        mCallManager.setStateChangeListener(new CallManager.State() {
            @Override
            public void change(final int state, final CallModel callModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == CallManager.IDLE) {
                            mOverlayView.hide();
                        } else if (state == CallManager.RINGING) {
                            mOverlayView.show();

                            mTxvState.setText(callModel.getFromWhere() + "來電:" + callModel.getName());

                            mBtnAcceptCall.setVisibility(View.VISIBLE);
                            mBtnAcceptCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mCallManager.acceptCall(callModel);
                                }
                            });

                            mBtnRejectCall.setVisibility(View.VISIBLE);
                            mBtnRejectCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mCallManager.rejectCall(callModel);
                                }
                            });
                        } else if (state == CallManager.OFFHOOK) {
                            mOverlayView.show();
                            mTxvState.setText(callModel.getFromWhere() + "通話中:" + callModel.getName());

                            mBtnAcceptCall.setVisibility(View.GONE);
                            mBtnAcceptCall.setOnClickListener(null);

                            mBtnRejectCall.setVisibility(View.VISIBLE);
                            mBtnRejectCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mCallManager.rejectCall(callModel);
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void createView() {
        mOverlayView = new OverlayView(this);
        mTxvState = mOverlayView.addTextView();
        mBtnAcceptCall = mOverlayView.addButton("接聽電話");
        mBtnRejectCall = mOverlayView.addButton("掛斷電話");
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

        if (mCallManager != null) {
            mCallManager.onDestroy();
        }
    }

    public void startPhoneListener(View view) {
        if (mCallManager != null) {
            mCallManager.enableListenPhoneState();
        }
    }

    public void stopPhoneListener(View view) {
        if (mCallManager != null) {
            mCallManager.disableListenPhoneState();
        }
    }
}
