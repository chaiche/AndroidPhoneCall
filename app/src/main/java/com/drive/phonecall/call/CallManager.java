package com.drive.phonecall.call;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.receive.FacebookReceive;
import com.drive.phonecall.receive.LineReceive;
import com.drive.phonecall.receive.PhoneReceive;

public class CallManager {

    public static final int IDLE = 1;
    public static final int RINGING = 2;
    public static final int OFFHOOK = 3;

    private Context mContext;

    private PhoneReceive mPhoneReceive;
    private LineReceive mLineReceive;
    private FacebookReceive mFacebookReceive;
    private State mState;

    public CallManager(Context context) {
        this.mContext = context;
    }

    public void enableListenPhoneState() {
        mPhoneReceive = new PhoneReceive(mContext);
        mPhoneReceive.setStateListener(new PhoneReceive.StateListener() {
            @Override
            public void change(final int state, final String number) {
                CallModel callModel = new CallModel();
                callModel.setName(number);
                callModel.setFromWhere(CallModel.PHONE);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        changeState(IDLE, callModel);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        changeState(OFFHOOK, callModel);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        changeState(RINGING, callModel);
                        break;
                }
            }
        });

        mPhoneReceive.start();
    }

    public void disableListenPhoneState() {
        if (mPhoneReceive != null) {
            mPhoneReceive.setStateListener(null);
            mPhoneReceive.stop();
        }
    }

    public void enableLineService() {
        mLineReceive = new LineReceive(mContext);
        mLineReceive.setStateListener(new LineReceive.StateListener() {
            @Override
            public void change(int state, CallModel callModel) {
                switch (state) {
                    case LineReceive.CALL_HANG_OUT:
                        changeState(IDLE, callModel);
                        break;
                    case LineReceive.CALL_OFF_HOOK:
                        changeState(OFFHOOK, callModel);
                        break;
                    case LineReceive.CALL_INCOMING:
                        changeState(RINGING, callModel);
                        break;
                    case LineReceive.CALL_OUT_GOING:
                        changeState(RINGING, callModel);
                        break;
                }
            }
        });
        mLineReceive.start();
    }

    public void disableLineService() {
        if (mLineReceive != null) {
            mLineReceive.setStateListener(null);
            mLineReceive.stop();
        }
    }

    public void enableFbService() {
        mFacebookReceive = new FacebookReceive(mContext);
        mFacebookReceive.setStateListener(new FacebookReceive.StateListener() {
            @Override
            public void change(int state, CallModel callModel) {
                switch (state) {
                    case FacebookReceive.CALL_HANG_OUT:
                        changeState(IDLE, callModel);
                        break;
                    case FacebookReceive.CALL_OFF_HOOK:
                        changeState(OFFHOOK, callModel);
                        break;
                    case FacebookReceive.CALL_INCOMING:
                        changeState(RINGING, callModel);
                        break;
                }
            }
        });
        mFacebookReceive.start();
    }

    public void disableFbService() {
        if (mFacebookReceive != null) {
            mFacebookReceive.setStateListener(null);
            mFacebookReceive.stop();
        }
    }

    public boolean rejectCall(CallModel callModel) {
        if (callModel != null) {
            switch (callModel.getFromWhere()) {
                case CallModel.PHONE:
                    return mPhoneReceive.hangOutPhone();
                case CallModel.LINE:
                    return mLineReceive.rejectCall();
                case CallModel.FB:
                    return mFacebookReceive.rejectCall();
            }
        }

        return false;
    }

    public boolean acceptCall(CallModel callModel) {
        if (callModel != null) {
            switch (callModel.getFromWhere()) {
                case CallModel.PHONE:
                    return mPhoneReceive.acceptCall();
                case CallModel.LINE:
                    mLineReceive.acceptCall();
                    return true;
                case CallModel.FB:
                    mFacebookReceive.acceptCall();
                    return true;
            }
        }

        return false;
    }

    public void setStateChangeListener(State stateChangeListener) {
        this.mState = stateChangeListener;
    }

    private void changeState(int state, CallModel callModel) {
        if (mState != null) {
            mState.change(state, callModel);
        }
    }

    public interface State {
        void change(int state, CallModel callModel);
    }

    public void onDestroy() {
        disableListenPhoneState();
        disableLineService();
        disableFbService();
    }
}
