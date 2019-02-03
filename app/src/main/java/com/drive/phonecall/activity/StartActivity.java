package com.drive.phonecall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drive.phonecall.BaseActivity;
import com.drive.phonecall.R;
import com.drive.phonecall.task.PermissionTask;

import butterknife.BindView;

public class StartActivity extends BaseActivity {

    @BindView(R.id.lin_title) LinearLayout mLinTitle;
    @BindView(R.id.fl_content) FrameLayout mFlContent;
    @BindView(R.id.fl_button) FrameLayout mFlButton;
    @BindView(R.id.igv_icon) ImageView mIgvIcon;
    @BindView(R.id.txv_title) TextView mTxvTitle;
    @BindView(R.id.txv_content) TextView mTxvContent;
    @BindView(R.id.txv_button) TextView mTxvButton;

    private PermissionTask mPermissionTask;
    public static final int PHONE_CODE = 101;

    @Override
    protected void initial(Bundle savedInstanceState) {
        mPermissionTask = new PermissionTask(this);
    }

    @Override
    protected View onCreateView() {
        return View.inflate(this, R.layout.activity_start, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPermission();
    }

    private void checkPermission() {
        if (!mPermissionTask.isPhonePmGranted()) {
            requestPhonePermission();
            return;
        }

        if (!mPermissionTask.isOverLayGranted()) {
            requestOverLayPermission();
            return;
        }

        if (!mPermissionTask.isNotificationGranted()) {
            requestNotificationPm();
            return;
        }

        startMain();
    }

    public void requestPhonePermission() {
        mTxvTitle.setText(R.string.phone_pm);
        mIgvIcon.setImageResource(R.drawable.twotone_phone_black_48);
        mTxvContent.setText(R.string.phone_pm_content);
        mTxvButton.setText(R.string.enter);
        mFlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(StartActivity.this,
                        mPermissionTask.getPhonePermission(),
                        PHONE_CODE);
            }
        });
    }

    public void requestOverLayPermission() {
        mTxvTitle.setText(R.string.overlay_pm);
        mIgvIcon.setImageResource(R.drawable.twotone_insert_photo_black_48);
        mTxvContent.setText(R.string.overlay_pm_content);
        mTxvButton.setText(R.string.enter);
        mFlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionTask.requestOverLayPm();
            }
        });
    }

    public void requestNotificationPm() {
        mTxvTitle.setText(R.string.notification_pm);
        mIgvIcon.setImageResource(R.drawable.twotone_phone_callback_black_48);
        mTxvContent.setText(R.string.notification_pm_content);
        mTxvButton.setText(R.string.enter);
        mFlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionTask.requestNotificationPm();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PHONE_CODE:
                checkPermission();
        }
    }

    public void startMain() {
        finish();

        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

}
