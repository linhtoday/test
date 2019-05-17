package com.meow.quanly.model;

public class TaskItem {
    public boolean check;
    public String detail;
    public int type;

    public TaskItem() {
    }

    public TaskItem(boolean check, String detail, int type) {
        this.check = check;
        this.detail = detail;
        this.type = type;
    }
}
