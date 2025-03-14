package com.moutamid.uchannelboostadmin.models;

public class ArrayModel {
    private int value;
    private String key;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayModel(int value, String key) {
        this.value = value;
        this.key = key;
    }

    public ArrayModel() {
    }
}
