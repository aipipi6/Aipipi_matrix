package com.example.aipipi.ble.entity;


public abstract class BaseBleMsgEntity {
    private int type;

    public BaseBleMsgEntity(int type) {
        this.type = type;
    }

    public abstract void convert(int start, int len, byte[] datas);

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
