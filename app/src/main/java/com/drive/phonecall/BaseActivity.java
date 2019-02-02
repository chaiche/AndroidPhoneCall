package com.drive.phonecall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;
import butterknife.ButterKnife;

@SuppressWarnings("unused")
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected Toast mToast = null;
    protected Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onCreateView());
        ButterKnife.bind(this);

        mContext = this;
        initial(savedInstanceState);
    }

    protected abstract void initial(Bundle savedInstanceState);

    protected abstract View onCreateView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @SuppressLint("ShowToast")
    protected void showToast(String message) {
        if (mToast == null) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        } else {
            mToast.setText(message);
        }

        mToast.show();
    }

    protected void showTracking() {

    }
}