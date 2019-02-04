package com.drive.phonecall.receive;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PhoneReceive {

    private static final String TAG = PhoneReceive.class.getSimpleName();

    private Context mContext;

    private TelephonyManager mManager;
    private PhoneState mPhoneState;

    private StateListener mStateListener;

    private String[] REJECT_CALL = {"忽略", "拒絕", "REJECT", "Dismiss", "拒接", "Decline"};

    public PhoneReceive(Context context) {
        this.mContext = context;

        mManager = ((TelephonyManager) Objects.requireNonNull(context.getSystemService(Context.TELEPHONY_SERVICE)));
    }

    public void start() {
        mPhoneState = new PhoneState();
        mManager.listen(mPhoneState, PhoneState.LISTEN_CALL_STATE);
    }

    public void stop(){
        try {
            if (mPhoneState != null) {
                mManager.listen(mPhoneState, PhoneState.LISTEN_NONE);
                mPhoneState = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStateListener(StateListener stateListener){
        this.mStateListener = stateListener;
    }

    private class PhoneState extends PhoneStateListener {

        @SuppressLint("CheckResult")
        @Override
        public void onCallStateChanged(int state, final String number) {
            super.onCallStateChanged(state, number);

            Log.i(TAG, "onCallStateChanged : " + state + ", " + number);
            if(mStateListener != null){
                mStateListener.change(state, number);
            }
        }
    }

    public interface StateListener {
        void change(final int state, final String number);
    }

    @SuppressLint("MissingPermission")
    public boolean acceptCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "acceptCall TelecomManager");
                TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
                if (telecomManager != null) {
                    telecomManager.acceptRingingCall();
                }

                return true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return acceptCallUseMediaController();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return acceptCallUseAudioManager();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return throughReceiver();
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean acceptCallUseMediaController() {
        Log.i(TAG, "acceptCallUseMediaController");

        MediaSessionManager mediaSessionManager = (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
        try {
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(new ComponentName(mContext, NotificationReceiver.class));
            for (MediaController controller : controllers) {
                if ("com.android.server.telecom".equals(controller.getPackageName())) {
                    controller.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    return true;
                }
            }
        } catch (Exception e) {
            return acceptCallUseAudioManager();
        }

        return false;
    }

    private boolean acceptCallUseAudioManager() {
        Log.i(TAG, "acceptCallUseAudioManager");

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
        audioManager.dispatchMediaKeyEvent(downEvent);
        audioManager.dispatchMediaKeyEvent(upEvent);

        return true;
    }

    private ITelephony getTelephonyService() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            return (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean throughTelephonyService() {
        Log.i(TAG, "throughTelephonyService");
        ITelephony telephonyService = getTelephonyService();
        if (telephonyService != null) {
            try {
                telephonyService.silenceRinger();
                telephonyService.answerRingingCall();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private boolean throughReceiver() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        try {
            return throughTelephonyService();
        } catch (Exception exception) {
            boolean broadcastConnected = "HTC".equalsIgnoreCase(Build.MANUFACTURER)
                    && !audioManager.isWiredHeadsetOn();

            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
            try {
                Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK);
                Log.i(TAG, "throughReceiver");
            } catch (IOException ioe) {
                throughPhoneHeadsetHook();
            } finally {
                if (broadcastConnected) {
                    return broadcastHeadsetConnected(false);
                }
            }

            return true;
        }
    }

    private boolean broadcastHeadsetConnected(boolean connected) {
        Intent intent = new Intent(Intent.ACTION_HEADSET_PLUG);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra("state", connected ? 1 : 0);
        intent.putExtra("name", "mysms");
        try {
            mContext.sendOrderedBroadcast(intent, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private boolean throughPhoneHeadsetHook() {
        Log.i(TAG, "throughPhoneHeadsetHook");

        Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        mContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        mContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

        return true;
    }

    public boolean hangOutPhone(){
        Log.i(TAG, "hangOutPhone");

//        try {
//            TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
//            if (telecomManager != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    telecomManager.endCall();
//                }
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return hangOutPhoneUseITelephony();
    }

    private boolean hangOutPhoneUseITelephony(){
        Log.i(TAG, "hangOutPhoneUseITelephony");

        TelephonyManager mTelMgr = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony;
            System.out.println("End call.");
            iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelMgr, (Object[]) null);
            iTelephony.endCall();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail to answer ring call.");
        }

        return hangOutPhoneUseNotification();
    }

    private boolean hangOutPhoneUseNotification() {
        Log.i(TAG, "hangOutPhoneUseNotification");

        StatusBarNotification[] sbs = NotificationReceiver.getCurrentActiveNotifications(mContext);
        for (StatusBarNotification sb : sbs) {
            if (getControlPackNames().contains(sb.getPackageName())) {
                try {
                    if (sb.getNotification().actions != null) {
                        for (Notification.Action action : sb.getNotification().actions) {
                            String ac = action.title.toString();
                            if (Arrays.asList(REJECT_CALL).contains(ac)) {
                                PendingIntent intent = action.actionIntent;
                                try {
                                    intent.send();

                                    return true;
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private List<String> getControlPackNames() {
        List<String> list = new ArrayList<>();
        list.add("com.android.incallui");
        list.add("com.google.android.dialer");

        return list;
    }
}
