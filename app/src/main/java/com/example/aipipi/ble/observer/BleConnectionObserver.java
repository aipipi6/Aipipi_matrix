package com.example.aipipi.ble.observer;

import android.bluetooth.BluetoothDevice;

/**
 * Created by chenjun on 2018/4/24.
 */

public interface BleConnectionObserver {
    void onStartConnect();

    void onConnectSuccess(BluetoothDevice device);

    void onConnectFailed(BluetoothDevice device);

    void onDisConnected(BluetoothDevice device);
}
