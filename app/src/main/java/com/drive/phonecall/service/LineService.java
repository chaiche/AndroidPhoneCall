package com.drive.phonecall.service;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;

import java.util.List;

public class LineService {

    public static final String TAG = LineService.class.getSimpleName();

    public static final int CALL_INCOMING = 1;
    public static final int CALL_OFF_HOOK = 2;
    public static final int CALL_HANG_OUT = 3;

    private Context mContext;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private MediaSessionManager mMediaSessionManager;
    private StateListener mStateListener;

    private int mLastState;
    private boolean mWaitHangUp = false;

    public LineService(Context context) {
        this.mContext = context;
        mMediaSessionManager = (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    private void enableThread() {
        mHandlerThread = new HandlerThread(LineService.class.getSimpleName());
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
        NotificationReceiver.setLineReceive(new NotificationReceiver.Receive() {
            @Override
            public void post(StatusBarNotification notification, String packName, String name, String message) {

                if (notification.getNotification().actions != null) {
                    for (Notification.Action action : notification.getNotification().actions) {
                        Log.i(TAG, "action : " + action.title);
                    }
                }

                if ("LINE語音通話來電中…".equals(message)
                        || "Incoming LINE voice call".equals(message)
                        || "LINE语音通话来电...".equals(message)) {
                    if (enableCheckLineCallUp()) {
                        if (mStateListener != null) {
                            mWaitHangUp = false;
                            mLastState = CALL_INCOMING;
                            mStateListener.change(CALL_INCOMING, name);
                        }
                    }
                } else if ("LINE語音通話撥打中…".equals(message)
                        || "正在拨打LINE语音通话...".equals(message)
                        || "正在拨打LINE语音通话...".equals(message)) {

                } else if ("LINE通話中".equals(message)
                        || "LINE call in progress".equals(message)
                        || "正在进行LINE通话".equals(message)) {
                    if (mWaitHangUp) {
                        rejectCall();
                    } else if (enableCheckLineCallUp()) {
                        if (mStateListener != null) {
                            mLastState = CALL_OFF_HOOK;
                            mStateListener.change(CALL_OFF_HOOK, name);
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
                mWaitHangUp = false;
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
        NotificationReceiver.setLineReceive(null);
        disableThread();
    }

    public void setStateListener(StateListener stateListener) {
        this.mStateListener = stateListener;
    }

    public interface StateListener {
        void change(final int state, final String name);
    }


    public void setWaitHangUp(boolean b) {
        this.mWaitHangUp = b;
    }

    public void rejectCall() {
        if (mLastState == CALL_INCOMING) {
            setWaitHangUp(true);
        }

        dispatchMediaButton();
    }

    public void acceptCall() {
        dispatchMediaButton();
    }

    private void dispatchMediaButton() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                List<MediaController> controllers = mMediaSessionManager.getActiveSessions(new ComponentName(mContext, NotificationReceiver.class));
                for (MediaController controller : controllers) {
                    if (getControlPackName().equals(controller.getPackageName())) {
                        controller.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getControlPackName() {
        return "jp.naver.line.android";
    }
}
