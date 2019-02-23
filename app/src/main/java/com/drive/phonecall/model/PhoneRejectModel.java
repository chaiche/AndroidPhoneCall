package com.drive.phonecall.model;

import android.app.Notification;

public class PhoneRejectModel {

    private String name;
    private Notification.Action rejectAction;

    public String getName() {
        return name;
    }

    public PhoneRejectModel setName(String name) {
        this.name = name;
        return this;
    }

    public Notification.Action getRejectAction() {
        return rejectAction;
    }

    public PhoneRejectModel setRejectAction(Notification.Action rejectAction) {
        this.rejectAction = rejectAction;
        return this;
    }
}
