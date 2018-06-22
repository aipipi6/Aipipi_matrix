package com.example.aipipi.ble.observer;

import android.bluetooth.BluetoothDevice;

/**
 * Created by chenjun on 2018/4/24.
 */

public interface BleScanObserver {

    void onScanStart();

    void onScanDevice(BluetoothDevice device);

    void onScanFinished();
}
