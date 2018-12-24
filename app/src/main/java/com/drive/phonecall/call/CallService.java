package com.drive.phonecall.call;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drive.phonecall.CallManager;
import com.drive.phonecall.OverlayView;
import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.service.NotificationReceiver;

public class CallService extends Service {

    public static final String TAG = CallService.class.getSimpleName();

    private OverlayView mOverlayView;
    private CallManager mCallManager;

    private TextView mTxvState;
    private Button mBtnAcceptCall;
    private Button mBtnRejectCall;

    private CallServiceBinder mBinder;
    private Handler mHandler;

    public CallService() {
    }

    public IBinder onBind(Intent intent) {
        mBinder = new CallServiceBinder();
        return mBinder;
    }

    public class CallServiceBinder extends Binder {
        public CallService getService() {
            return CallService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCallManager = new CallManager(this);
        createView();
        NotificationReceiver.executeIfNotRunningAndRequestRebind(this);

        mHandler = new Handler(getMainLooper());

        mCallManager.setStateChangeListener(new CallManager.State() {
            @Override
            public void change(final int state, final CallModel callModel) {
                Log.i(TAG, "change state : " + state + ", which :" + callModel.getFromWhere() + ", name : " + callModel.getName());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run");
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

        startFbListener();

    }

    private void createView() {
        mOverlayView = new OverlayView(this);
        mTxvState = mOverlayView.addTextView();
        mBtnAcceptCall = mOverlayView.addButton("接聽電話");
        mBtnRejectCall = mOverlayView.addButton("掛斷電話");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void startPhoneListener() {
        if (mCallManager != null) {
            Log.i(TAG, "startPhoneListener");
            mCallManager.enableListenPhoneState();
        }
    }

    public void stopPhoneListener() {
        if (mCallManager != null) {
            Log.i(TAG, "stopPhoneListener");
            mCallManager.disableListenPhoneState();
        }
    }

    public void startLineListener() {
        if (mCallManager != null) {
            Log.i(TAG, "startLineListener");
            mCallManager.enableLineService();
        }
    }

    public void stopLineListener() {
        if (mCallManager != null) {
            Log.i(TAG, "stopLineListener");
            mCallManager.disableLineService();
        }
    }

    public void startFbListener() {
        if (mCallManager != null) {
            Log.i(TAG, "startLineListener");
            mCallManager.enableFbService();
        }
    }

    public void stopFbListener() {
        if (mCallManager != null) {
            Log.i(TAG, "stopLineListener");
            mCallManager.disableFbService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy");
        if (mCallManager != null) {
            mCallManager.onDestroy();
        }

        if (mOverlayView != null) {
            mOverlayView.onDestroy();
        }
    }
}
