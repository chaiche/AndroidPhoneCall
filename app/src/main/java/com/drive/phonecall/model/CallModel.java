package com.drive.phonecall.model;

import android.graphics.Bitmap;

public class CallModel {

    // fromWhere
    public static final String PHONE = "電話";
    public static final String LINE = "Line";
    public static final String FB = "Facebook";

    private String name;
    private String message;
    private String fromWhere;
    private Bitmap icon;

    public String getName() {
        return name;
    }

    public CallModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getFromWhere() {
        return fromWhere;
    }

    public CallModel setFromWhere(String fromWhere) {
        this.fromWhere = fromWhere;
        return this;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public CallModel setIcon(Bitmap icon) {
        this.icon = icon;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CallModel setMessage(String message) {
        this.message = message;
        return this;
    }
}
