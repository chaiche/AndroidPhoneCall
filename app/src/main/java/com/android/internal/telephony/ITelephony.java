package com.android.internal.telephony;

import android.support.annotation.Keep;

@Keep
public interface ITelephony {

    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}