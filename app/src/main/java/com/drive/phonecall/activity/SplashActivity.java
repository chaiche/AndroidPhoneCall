package com.drive.phonecall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.drive.phonecall.BaseActivity;
import com.drive.phonecall.R;
import com.drive.phonecall.task.PermissionTask;
import com.drive.phonecall.widget.CustomTypeTextAnimationView;

import butterknife.BindString;
import butterknife.BindView;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.cttv_app_name) CustomTypeTextAnimationView mCttvAppName;
    @BindString(R.string.app_name) String STR_APP_NAME;

    @Override
    protected void initial(Bundle savedInstanceState) {

        mCttvAppName.startPlayText(STR_APP_NAME, new CustomTypeTextAnimationView.AnimationDownListener() {
            @Override
            public void done() {
                openStart();
            }
        });
    }

    private void openStart() {
        finish();

        PermissionTask permissionTask = new PermissionTask(this);
        if (permissionTask.isAllPmGranted(this)) {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
        } else {
            Intent it = new Intent(this, StartActivity.class);
            startActivity(it);
        }
    }

    @Override
    protected View onCreateView() {
        return View.inflate(this, R.layout.activity_splash, null);
    }
}
