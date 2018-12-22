package com.drive.phonecall.simple;

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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.drive.phonecall.service.NotificationReceiver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class CallManager {

    private static final String TAG = CallManager.class.getSimpleName();
}
