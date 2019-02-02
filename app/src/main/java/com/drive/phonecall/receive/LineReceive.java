package com.drive.phonecall.receive;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.drive.phonecall.model.CallModel;

import java.util.List;

public class LineReceive {

    public static final String TAG = LineReceive.class.getSimpleName();

    public static final int CALL_INCOMING = 1;
    public static final int CALL_OFF_HOOK = 2;
    public static final int CALL_HANG_OUT = 3;
    public static final int CALL_OUT_GOING = 4;

    private Context mContext;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private MediaSessionManager mMediaSessionManager;
    private StateListener mStateListener;

    private int mLastState;

    public LineReceive(Context context) {
        this.mContext = context;
        mMediaSessionManager = (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    private void enableThread() {
        mHandlerThread = new HandlerThread(LineReceive.class.getSimpleName());
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private void disableThread() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mCheckLineCallRunnable);
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
            public void post(StatusBarNotification notification, String packName, CallModel callModel) {
                callModel.setFromWhere(CallModel.LINE);

                String message = callModel.getMessage();
                if ("LINE語音通話來電中…".equals(message)
                        || "Incoming LINE voice call".equals(message)
                        || "LINE语音通话来电...".equals(message)) {
                    if (enableCheckLineCallUp()) {
                        if (mStateListener != null) {
                            mLastState = CALL_INCOMING;
                            mStateListener.change(CALL_INCOMING, callModel);
                        }
                    }
                } else if ("LINE語音通話撥打中…".equals(message)
                        || "正在拨打LINE语音通话...".equals(message)
                        || "正在拨打LINE语音通话...".equals(message)) {
                    if (enableCheckLineCallUp()) {
                        if (mStateListener != null) {
                            mLastState = CALL_OUT_GOING;
                            mStateListener.change(CALL_OUT_GOING, callModel);
                        }
                    }
                } else if ("LINE通話中".equals(message)
                        || "LINE call in progress".equals(message)
                        || "正在进行LINE通话".equals(message)) {
                   if (enableCheckLineCallUp()) {
                        if (mStateListener != null) {
                            mLastState = CALL_OFF_HOOK;
                            mStateListener.change(CALL_OFF_HOOK, callModel);
                        }
                    }
                }
            }
        });
    }

    private Runnable mCheckLineCallRunnable = new Runnable() {

        @Override
        public void run() {
            if (hasJpLine()) {
                mHandler.postDelayed(this, 200);
            } else {
                mLastState = CALL_HANG_OUT;
                mStateListener.change(CALL_HANG_OUT, null);
            }
        }
    };

    private boolean hasJpLine() {
        List<MediaController> controllers = mMediaSessionManager.getActiveSessions(new ComponentName(mContext, NotificationReceiver.class));
        for (MediaController controller : controllers) {
            if (getControlPackName().equals(controller.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    private boolean enableCheckLineCallUp() {
        mHandler.removeCallbacks(mCheckLineCallRunnable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaSessionManager mediaSessionManager = (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
            boolean hasJpLine = false;
            for (MediaController controller : mediaSessionManager.getActiveSessions(new ComponentName(mContext, NotificationReceiver.class))) {
                if (getControlPackName().equals(controller.getPackageName())) {
                    hasJpLine = true;
                }
            }
            if (!hasJpLine) {
                return false;
            }

            mHandler.postDelayed(mCheckLineCallRunnable, 1000);
            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        NotificationReceiver.unregisterReceive(getControlPackName());
        disableThread();
    }

    public void setStateListener(StateListener stateListener) {
        this.mStateListener = stateListener;
    }

    public interface StateListener {
        void change(final int state, final CallModel callModel);
    }


    public boolean rejectCall() {
        Log.i(TAG, "rejectCall");
        StatusBarNotification[] statusBarNotifications = NotificationReceiver.getCurrentActiveNotifications(mContext);
        if (statusBarNotifications != null) {
            for (StatusBarNotification barNotification : statusBarNotifications) {
                if (barNotification.getPackageName().equals(getControlPackName())) {
                    for (Notification.Action action : barNotification.getNotification().actions) {
                        if ("拒絕".equals(action.title) || "結束通話".equals(action.title)) {
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

    public boolean acceptCall() {
        Log.i(TAG, "acceptCall");
        StatusBarNotification[] statusBarNotifications = NotificationReceiver.getCurrentActiveNotifications(mContext);
        if (statusBarNotifications != null) {
            Log.i(TAG, String.valueOf(statusBarNotifications.length));
            for (StatusBarNotification barNotification : statusBarNotifications) {
                if (barNotification.getPackageName().equals(getControlPackName())) {
                    for (Notification.Action action : barNotification.getNotification().actions) {
                        if ("接聽".equals(action.title)) {
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
        return "jp.naver.line.android";
    }
}
