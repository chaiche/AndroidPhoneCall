package com.drive.phonecall.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.drive.phonecall.R;

import java.util.Arrays;

public class FacebookReceive {

    public static final int CALL_INCOMING = 1;
    public static final int CALL_OFF_HOOK = 2;
    public static final int CALL_HANG_OUT = 3;

    public static final String TAG = FacebookReceive.class.getSimpleName();
    public Context mContext;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private StateListener mStateListener;

    private int mLastState;

    private String[] GET_CALL = {"來自Messenger的通話", "Calling from Messenger", "正在通过Messenger呼叫"};
    private String[] CALL_OFFHOOK = {"點按即可繼續通話 • 連線中⋯⋯",
            "點按即可返回通話畫面 • 連線中⋯⋯", "點按即可返回通話畫面",
            "Tap to return to call • Connecting…",
            "Tap to return to call",
            "轻触返回通话• 连线中...",
            "轻触返回通话"};

    private String[] REJECT_CALL = {"拒絕", "結束通話", "DECLINE", "End call", "拒绝", "结束通话"};
    private String[] ANSWER_CALL = {"回覆", "ANSWER", "回答"};

    public FacebookReceive(Context context) {
        this.mContext = context;
    }

    private void enableThread() {
        mHandlerThread = new HandlerThread(LineReceive.class.getSimpleName());
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private void disableThread() {
        if (mHandler != null) {
            if (mHandlerThread != null) {
                mHandlerThread.quitSafely();
                mHandlerThread = null;
            }
            mHandler = null;
        }
    }

    public void start() {
        enableThread();
        NotificationReceiver.registerReceive(getControlPackName(), new NotificationReceiver.Receive() {
            @Override
            public void post(StatusBarNotification notification, String packName, String name, String message) {
                Log.i(TAG, "post");

                if (Arrays.asList(GET_CALL).contains(message)) {
                    if (mStateListener != null) {
                        mLastState = CALL_INCOMING;
                        mStateListener.change(CALL_INCOMING, name);
                    }
                } else if (Arrays.asList(CALL_OFFHOOK).contains(message)) {
                    if (mStateListener != null) {
                        mLastState = CALL_OFF_HOOK;
                        mStateListener.change(CALL_OFF_HOOK, name);
                    }
                } else {
                    String tmp = message.replace(name, "");
                    if (tmp.equals("你錯過了的來電。")) {
                        mLastState = CALL_HANG_OUT;
                        mStateListener.change(CALL_HANG_OUT, name);
                        return;
                    }

                    if (mLastState != CALL_HANG_OUT) {
                        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(message)) {
                            mLastState = CALL_HANG_OUT;
                            mStateListener.change(CALL_HANG_OUT, null);
                            return;
                        }
                    }
                }
            }
        });

    }

    public void stop() {
        NotificationReceiver.unregisterReceive(getControlPackName());
        disableThread();
    }

    public void setStateListener(StateListener stateListener) {
        this.mStateListener = stateListener;
    }

    public interface StateListener {
        void change(final int state, final String name);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean rejectCall() {
        Log.i(TAG, "rejectCall");
        StatusBarNotification[] statusBarNotifications = NotificationReceiver.getCurrentActiveNotifications(mContext);
        if (statusBarNotifications != null) {
            for (StatusBarNotification barNotification : statusBarNotifications) {
                if (barNotification.getPackageName().equals(getControlPackName())) {
                    for (Notification.Action action : barNotification.getNotification().actions) {
                        if (Arrays.asList(REJECT_CALL).contains(action.title)) {
                            Log.i(TAG, "execute rejectCall");
                            try {
                                action.actionIntent.send();
                                return true;
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean acceptCall() {
        Log.i(TAG, "acceptCall");
        StatusBarNotification[] statusBarNotifications = NotificationReceiver.getCurrentActiveNotifications(mContext);
        if (statusBarNotifications != null) {
            Log.i(TAG, String.valueOf(statusBarNotifications.length));
            for (StatusBarNotification barNotification : statusBarNotifications) {
                if (barNotification.getPackageName().equals(getControlPackName())) {
                    for (Notification.Action action : barNotification.getNotification().actions) {
                        if (Arrays.asList(ANSWER_CALL).contains(action.title)) {
                            try {
                                Log.i(TAG, "execute acceptCall");
                                action.actionIntent.send();
                                return true;
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private String getControlPackName() {
        return mContext.getResources().getString(R.string.fb_package);
    }
}
