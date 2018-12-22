package com.drive.phonecall.service;

import android.annotation.SuppressLint;
import android.app.RemoteInput;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.util.Log;

import com.drive.phonecall.utils.SystemUtils;

@SuppressWarnings("unused")
public class NotificationReceiver extends NotificationListenerService {

    private final static String TAG = NotificationReceiver.class.getSimpleName();

    private static final String TAG_WEARABLE = "android.wearable.EXTENSIONS";
    private static final String TAG_ACTIONS = "actions";

    private static NotificationReceiver mNotificationReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");
    }

    /**
     * rebind 必須實現這個function
     */
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommandCalled");
        reOpenCheck();

        return START_STICKY;
    }

    @SuppressWarnings("unchecked")
    @SuppressLint("CheckResult")
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        super.onNotificationPosted(notification);

        Bundle bundle = notification.getNotification().extras;
        if (bundle == null) {
            return;
        }

        Object titleO = bundle.get("android.title");
        Object textO = bundle.get("android.text");
        String name = "";
        String message = "";
        if (titleO != null && titleO instanceof String) {
            name = titleO.toString();
        } else if (titleO != null && titleO instanceof SpannableString) {
            name = ((SpannableString) titleO).toString();
        }

        if (textO != null && textO instanceof String) {
            message = textO.toString();
        } else if (textO != null && textO instanceof SpannableString) {
            message = ((SpannableString) textO).toString();
        }

        Log.e(TAG, "name :" + name + ", message : " + message + ", pack : " + notification.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        super.onNotificationRemoved(notification);

    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "onListenerConnected");
        mNotificationReceiver = this;
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "onListenerDisconnected");
        mNotificationReceiver = null;
    }

    public static StatusBarNotification[] getCurrentActiveNotifications(Context context) {
        executeIfNotRunningAndRequestRebind(context);

        if (mNotificationReceiver == null) {
            return new StatusBarNotification[]{};
        }
        return mNotificationReceiver.getActiveNotifications();
    }

    @SuppressLint("NewApi")
    private void getDetailsOfNotification(RemoteInput remoteInput) {

        String resultKey = remoteInput.getResultKey();
        String label = remoteInput.getLabel().toString();
        Boolean canFreeForm = remoteInput.getAllowFreeFormInput();

        Log.i("resultkey", resultKey);
        Log.i("label", label);
        Log.i("canFreeForm", canFreeForm + "");

        if (remoteInput.getChoices() != null && remoteInput.getChoices().length > 0) {
            String[] possibleChoices = new String[remoteInput.getChoices().length];
            for (int i = 0; i < remoteInput.getChoices().length; i++) {
                possibleChoices[i] = remoteInput.getChoices()[i].toString();
                Log.i("1", possibleChoices[i]);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy");
    }

    public static void executeIfNotRunningAndRequestRebind(Context context) {
        Log.i(TAG, "executeIfNotRunningAndRequestRebind check");
        boolean isRunning = SystemUtils.isServiceRunning(context, NotificationReceiver.class);
        if (!isRunning || mNotificationReceiver == null) {
            Log.i(TAG, "executeIfNotRunningAndRequestRebind : " + false);
            if (isRunning) {
                Log.i(TAG, "executeIfNotRunningAndRequestRebind " + "NotificationReceiver is Running");
                context.stopService(new Intent(context, NotificationReceiver.class));
            }
            context.startService(new Intent(context, NotificationReceiver.class));
        } else {
            Log.i(TAG, "executeIfNotRunningAndRequestRebind : " + true);
        }
    }

    private void reOpenCheck() {
        Log.i(TAG, "reOpen check");
        if (mNotificationReceiver == null) {
            Log.i(TAG, "execute reOpen");
            ComponentName componentName = new ComponentName(getApplicationContext(), NotificationReceiver.class);
            PackageManager packageManager = getPackageManager();
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requestRebind(new ComponentName(getApplicationContext(), NotificationReceiver.class));
            }
        }
    }
}
