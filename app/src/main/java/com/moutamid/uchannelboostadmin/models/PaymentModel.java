package com.moutamid.uchannelboostadmin.models;

public class PaymentModel {


    String userId, email, amount, imageLink, key, current_coins, need_coins, account_type, date, duration;
    boolean approve;

    public PaymentModel() {
    }

    public PaymentModel(String userId, String email, String amount, String imageLink, boolean approve) {
        this.userId = userId;
        this.email = email;
        this.amount = amount;
        this.imageLink = imageLink;
        this.approve = approve;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean isApprove() {
        return approve;
    }

    public String getKey() {
        return key;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCurrent_coins() {
        return current_coins;
    }

    public void setCurrent_coins(String current_coins) {
        this.current_coins = current_coins;
    }

    public String getNeed_coins() {
        return need_coins;
    }

    public void setNeed_coins(String need_coins) {
        this.need_coins = need_coins;
    }

    public String getAccount_type() {
        return account_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }
}