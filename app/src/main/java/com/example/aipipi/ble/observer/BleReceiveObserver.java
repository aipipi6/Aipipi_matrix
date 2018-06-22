package com.example.aipipi.ble.observer;


import com.example.aipipi.ble.entity.BaseBleMsgEntity;

/**
 * Created by chenjun on 2018/4/24.
 */

public interface BleReceiveObserver {
    void onReceive(BaseBleMsgEntity data);
}
