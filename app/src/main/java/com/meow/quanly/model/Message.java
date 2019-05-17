package com.meow.quanly.model;

public class Message {
    String mess, from, link;
    int type;
    long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Message() {
    }

    public Message(String mess, String from, String link, int type, long time) {
        this.mess = mess;
        this.from = from;
        this.link = link;
        this.type = type;
        this.time = time;
    }
}
