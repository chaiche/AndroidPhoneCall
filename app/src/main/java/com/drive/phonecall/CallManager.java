package com.drive.phonecall;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.service.FacebookService;
import com.drive.phonecall.service.LineService;
import com.drive.phonecall.service.PhoneService;

public class CallManager {

    public static final int IDLE = 1;
    public static final int RINGING = 2;
    public static final int OFFHOOK = 3;

    private Context mContext;

    private PhoneService mPhoneService;
    private LineService mLineService;
    private FacebookService mFacebookService;
    private State mState;

    public CallManager(Context context) {
        this.mContext = context;
    }

    public void enableListenPhoneState() {
        mPhoneService = new PhoneService(mContext);
        mPhoneService.setStateListener(new PhoneService.StateListener() {
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

        mPhoneService.start();
    }

    public void disableListenPhoneState() {
        if (mPhoneService != null) {
            mPhoneService.setStateListener(null);
            mPhoneService.stop();
        }
    }

    public void enableLineService(){
        mLineService = new LineService(mContext);
        mLineService.setStateListener(new LineService.StateListener() {
            @Override
            public void change(int state, String name) {
                CallModel callModel = new CallModel();
                callModel.setName(name);
                callModel.setFromWhere(CallModel.LINE);

                switch (state) {
                    case LineService.CALL_HANG_OUT:
                        changeState(IDLE, callModel);
                        break;
                    case LineService.CALL_OFF_HOOK:
                        changeState(OFFHOOK, callModel);
                        break;
                    case LineService.CALL_INCOMING:
                        changeState(RINGING, callModel);
                        break;
                }
            }
        });
        mLineService.start();
    }

    public void disableLineService(){
        if(mLineService != null){
            mLineService.setStateListener(null);
            mLineService.stop();
        }
    }

    public void enableFbService(){
        mFacebookService = new FacebookService(mContext);
        mFacebookService.setStateListener(new FacebookService.StateListener() {
            @Override
            public void change(int state, String name) {
                CallModel callModel = new CallModel();
                callModel.setName(name);
                callModel.setFromWhere(CallModel.FB);

                switch (state) {
                    case FacebookService.CALL_HANG_OUT:
                        changeState(IDLE, callModel);
                        break;
                    case FacebookService.CALL_OFF_HOOK:
                        changeState(OFFHOOK, callModel);
                        break;
                    case FacebookService.CALL_INCOMING:
                        changeState(RINGING, callModel);
                        break;
                }
            }
        });
        mFacebookService.start();
    }

    public void disableFbService(){
        if(mFacebookService != null){
            mFacebookService.setStateListener(null);
            mFacebookService.stop();
        }
    }

    public boolean rejectCall(CallModel callModel){
        if(callModel != null){
            switch (callModel.getFromWhere()){
                case CallModel.PHONE:
                    return mPhoneService.hangOutPhone();
                case CallModel.LINE:
                    mLineService.rejectCall();
                    return true;
                case CallModel.FB:
                    mFacebookService.rejectCall();
                    return true;
            }
        }

        return false;
    }

    public boolean acceptCall(CallModel callModel) {
        if (callModel != null) {
            switch (callModel.getFromWhere()) {
                case CallModel.PHONE:
                    return mPhoneService.acceptCall();
                case CallModel.LINE:
                    mLineService.acceptCall();
                    return true;
                case CallModel.FB:
                    mFacebookService.acceptCall();
                    return true;
            }
        }

        return false;
    }

    public void setStateChangeListener(State stateChangeListener){
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

    public void onDestroy(){
        disableListenPhoneState();
        disableLineService();
        disableFbService();
    }
}
