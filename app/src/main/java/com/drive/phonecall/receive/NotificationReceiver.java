package com.drive.phonecall.receive;

import android.annotation.SuppressLint;
import android.app.Notification;
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
import android.text.TextUtils;
import android.util.Log;

import com.drive.phonecall.model.CallModel;
import com.drive.phonecall.utils.SystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class NotificationReceiver extends NotificationListenerService {

    private final static String TAG = NotificationReceiver.class.getSimpleName();

    private static final String TAG_WEARABLE = "android.wearable.EXTENSIONS";
    private static final String TAG_ACTIONS = "actions";

    private static NotificationReceiver mNotificationReceiver;

    private static Map<String, Receive> mReceiveMap;

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

        String packName = notification.getPackageName();
        Log.e(TAG, "name :" + name + ", message : " + message + ", pack : " + packName);
        if (notification.getNotification().actions != null) {
            for (Notification.Action action : notification.getNotification().actions) {
                printAction("action 1", action);
            }
        }

        for (String s : bundle.keySet()) {
            if (TAG_WEARABLE.equals(s)) {
                Bundle bundle2 = ((Bundle) bundle.get(s));
                for (String s2 : Objects.requireNonNull(bundle2).keySet()) {
                    Object object = bundle2.get(s2);
                    if (s2 != null && object != null) {
                        if (TAG_ACTIONS.equals(s2) && object instanceof ArrayList) {
                            ArrayList<Notification.Action> actions = new ArrayList<>((ArrayList) object);
                            for (Notification.Action action : actions) {
                                printAction("action 2", action);
                            }
                        }
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(packName)) {
            if(mReceiveMap != null && mReceiveMap.containsKey(packName)){
                CallModel callModel = new CallModel();
                callModel.setName(name);
                callModel.setMessage(message);
                callModel.setIcon(notification.getNotification().largeIcon);
                mReceiveMap.get(packName).post(notification, packName, callModel);
            }
        }

    }

    private void printAction(String message, Notification.Action action){
        Log.i(TAG, message + " : " + String.valueOf(action.title)
                + ", actionIntent : " + (action.actionIntent == null)
                + ", bundle : " + (action.getExtras() == null)
                + ", remoteInputs : " + (action.getRemoteInputs() == null));

        if(action.getExtras()!= null){
            Log.i(TAG, "bundle -> ");
            Bundle b = action.getExtras();
            for (String s : b.keySet()) {
                Log.i(TAG, s + " : "+ b.get(s));
            }

            Log.i(TAG, "/////////");
        }

        if(action.getRemoteInputs()!= null){
            Log.i(TAG, "remoteInput -> ");
            for (RemoteInput remoteInput : action.getRemoteInputs()) {
                Log.i(TAG, "getResultKey :" + remoteInput.getResultKey());
            }

            Log.i(TAG, "/////////");
        }
    }

    public static void registerReceive(String packageName, Receive receive){
        if(mReceiveMap == null){
            mReceiveMap = new HashMap<>();
        }

        mReceiveMap.put(packageName, receive);
    }

    public static void unregisterReceive(String packageName) {
       if(mReceiveMap != null){
           mReceiveMap.remove(packageName);
       }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        super.onNotificationRemoved(notification);

    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "onListenerConnected");

        mNotificationReceiver = this;
        mReceiveMap = new HashMap<>();
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "onListenerDisconnected");
        mNotificationReceiver = null;

        if(mReceiveMap !=null) {
            mReceiveMap.clear();
            mReceiveMap = null;
        }
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

    public interface Receive {
        void post(StatusBarNotification notification, String packName, CallModel callModel);
    }
}
