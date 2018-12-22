package com.drive.phonecall;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.service.PhoneService;

public class CallManager {

    public static final int IDLE = 1;
    public static final int RINGING = 2;
    public static final int OFFHOOK = 3;

    private Context mContext;

    private PhoneService mPhoneService;
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

    public boolean rejectCall(CallModel callModel){
        if(callModel != null){
            switch (callModel.getFromWhere()){
                case CallModel.PHONE:
                    return mPhoneService.hangOutPhone();
            }
        }

        return false;
    }

    public boolean acceptCall(CallModel callModel){
        if(callModel != null){
            switch (callModel.getFromWhere()){
                case CallModel.PHONE:
                    return mPhoneService.acceptCall();
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
        if(mPhoneService != null){
            mPhoneService.setStateListener(null);
            mPhoneService.stop();
        }
    }
}
