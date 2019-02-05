package com.drive.phonecall.call;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.drive.phonecall.overlay.OverlayView;
import com.drive.phonecall.R;
import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.receive.NotificationReceiver;
import com.drive.phonecall.utils.LanguageUtils;

public class CallService extends Service {

    public static final String TAG = CallService.class.getSimpleName();

    private static final String CLOSE_SERVICE = "ACTION_CLOSE_SERVICE";
    public static final String ACTION_SERVICE_STATE_CHANGE = "ACTION_SERVICE_STATE_CHANGE";
    public static final String EXTRA_SERVICE_STATE_CHANGE = "EXTRA_SERVICE_STATE_CHANGE";

    private OverlayView mOverlayView;
    private CallManager mCallManager;

    private CallServiceBinder mBinder;
    private Handler mHandler;

    private NotificationManager mNotificationManager;
    private int AppNotificationId = 99901;
    private LocaleReceive mLocaleReceive;

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

        Intent it = new Intent(ACTION_SERVICE_STATE_CHANGE);
        it.putExtra(EXTRA_SERVICE_STATE_CHANGE, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(it);
        sendBroadcast(it);

        createNotification();

        mCallManager = new CallManager(this);
        createView();
        NotificationReceiver.executeIfNotRunningAndRequestRebind(this);

        mHandler = new Handler(getMainLooper());

        mCallManager.setStateChangeListener(new CallManager.State() {
            @Override
            public void change(final int state, final CallModel callModel) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (state == CallManager.IDLE) {
                            mOverlayView.hide();
                        } else if (state == CallManager.RINGING) {
                            mOverlayView.show();

                            mOverlayView.setUi(callModel.getFromWhere(),
                                    callModel.getName(),
                                    getResources().getString(R.string.call_incoming),
                                    callModel.getIcon());

                            mOverlayView.setAcceptControl(true, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mCallManager.acceptCall(callModel);
                                }
                            });

                            mOverlayView.setRejectControl(true, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mCallManager.rejectCall(callModel);
                                }
                            });
                        } else if (state == CallManager.OFFHOOK) {
                            mOverlayView.show();
                            mOverlayView.setUi(callModel.getFromWhere(),
                                    callModel.getName(),
                                    getResources().getString(R.string.call_listening),
                                    callModel.getIcon());

                            mOverlayView.setAcceptControl(false, null);

                            mOverlayView.setRejectControl(true, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mCallManager.rejectCall(callModel);
                                }
                            });
                        }
                    }
                });
            }
        });


        mCallManager.enableListenPhoneState();
        mCallManager.enableLineService();
        mCallManager.enableFbService();

        mLocaleReceive = new LocaleReceive();
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocaleReceive, new IntentFilter(LanguageUtils.ACTION_CHANGE_LANGUAGE));
    }

    private void createNotification() {
        String APP_NAME = getResources().getString(R.string.app_name);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                    new NotificationChannel(APP_NAME,
                            APP_NAME,
                            NotificationManager.IMPORTANCE_LOW)
            );
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, APP_NAME);
        builder.setSmallIcon(R.drawable.app_icon_simple)
                .setContentText(getResources().getString(R.string.notification_content));
        Intent intent = new Intent(this, CallService.class);
        intent.setAction(CLOSE_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(AppNotificationId, notification);
        mNotificationManager.notify(AppNotificationId, notification);
    }

    private void createView() {
        mOverlayView = new OverlayView(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (CLOSE_SERVICE.equals(intent.getAction())) {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class LocaleReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (LanguageUtils.ACTION_CHANGE_LANGUAGE.equals(intent.getAction())) {
                createNotification();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent it = new Intent(ACTION_SERVICE_STATE_CHANGE);
        it.putExtra(EXTRA_SERVICE_STATE_CHANGE, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(it);

        if (mCallManager != null) {
            mCallManager.onDestroy();
        }

        if (mOverlayView != null) {
            mOverlayView.onDestroy();
        }

        if (mNotificationManager != null) {
            mNotificationManager.cancel(AppNotificationId);
            mNotificationManager = null;
        }

        if(mLocaleReceive != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocaleReceive);
            mLocaleReceive = null;
        }
    }
}
