package com.drive.phonecall.model;

public class CallModel {

    // fromWhere
    public static final String PHONE = "電話";
    public static final String LINE = "Line";
    public static final String FB = "Facebook";

    private String name;
    private String fromWhere;

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
}
