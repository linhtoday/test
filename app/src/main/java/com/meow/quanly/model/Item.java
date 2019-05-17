package com.meow.quanly.model;

import java.io.Serializable;

public class Item implements Serializable {
   public String uid;
    public Boolean check;

    public Item() {
    }

    public Item(String uid, Boolean check) {
        this.uid = uid;
        this.check = check;
    }
}
