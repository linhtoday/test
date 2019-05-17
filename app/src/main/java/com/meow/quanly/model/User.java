package com.meow.quanly.model;

public class User {
    private String imageURL,key,username, uid, belong;
    private int type;

    public User() {
    }


    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User(String imageURL, String key, String username, String uid, String belong, int type) {
        this.imageURL = imageURL;
        this.key = key;
        this.username = username;
        this.uid = uid;
        this.belong = belong;
        this.type = type;
    }



    @Override
    public String toString() {
        return super.toString();
    }
}
