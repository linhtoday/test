package com.meow.quanly.model;

public class Notification {
    private String uid, mess, user_other;

    private long time;
    private boolean check;

    public Notification() {
    }

    public Notification(String uid, String mess, String user_other, long time, boolean check) {
        this.uid = uid;
        this.mess = mess;
        this.user_other = user_other;
        this.time = time;
        this.check = check;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getUser_other() {
        return user_other;
    }

    public void setUser_other(String user_other) {
        this.user_other = user_other;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
