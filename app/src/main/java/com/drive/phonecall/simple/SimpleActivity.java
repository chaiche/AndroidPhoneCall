package com.drive.phonecall.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.drive.phonecall.OverlayView;
import com.drive.phonecall.R;

import java.util.Objects;

public class SimpleActivity extends AppCompatActivity {

    public static final String TAG = SimpleActivity.class.getSimpleName();

    private TextView mTxvStatus;
    private PhoneState mPhoneState;
    private TelephonyManager mManager;

    private OverlayView mOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        mTxvStatus = findViewById(R.id.txv_status);

        mManager = ((TelephonyManager) Objects.requireNonNull(getSystemService(Context.TELEPHONY_SERVICE)));
        startPhoneState();

        createView();
    }

    private void createView() {
        mOverlayView = new OverlayView(this);

        mOverlayView.show();
    }

    private void startPhoneState() {
        mPhoneState = new PhoneState();
        mManager.listen(mPhoneState, PhoneState.LISTEN_CALL_STATE);
    }

    private void stopPhoneState() {
        try {
            if (mPhoneState != null) {
                mManager.listen(mPhoneState, PhoneState.LISTEN_NONE);
                mPhoneState = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PhoneState extends PhoneStateListener {

        @SuppressLint("CheckResult")
        @Override
        public void onCallStateChanged(int state, final String number) {
            super.onCallStateChanged(state, number);

            Log.i("PhoneStateListener", "onCallStateChanged : " + state + ", " + number);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mTxvStatus.post(new Runnable() {
                        @Override
                        public void run() {
                            mTxvStatus.setText("待機");
                        }
                    });
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    mTxvStatus.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            mTxvStatus.setText("通話中 :" + number);
                        }
                    });
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    mTxvStatus.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            mTxvStatus.setText("響鈴中 :" + number);
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mOverlayView != null) {
            mOverlayView.onDestroy();
        }
        stopPhoneState();
    }
}
