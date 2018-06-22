package com.example.aipipi.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.example.aipipi.ble.entity.BaseBleMsgEntity;
import com.example.aipipi.ble.observer.BleConnectionObserver;
import com.example.aipipi.ble.observer.BleReceiveObserver;
import com.example.aipipi.ble.observer.BleScanObserver;
import com.example.aipipi.ble.parser.BleCallBack;
import com.example.aipipi.ble.parser.IBleParser;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class BleManager {
    private static final String TAG = BleManager.class.getSimpleName();
    private static final UUID UUID_COM = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int STATE_NONE = 0x11;            // 空状态

    public static final int STATE_CONNECTING = 0xAA;      // 连接设备中
    public static final int STATE_CONNECTED = 0xBB;       // 已连接设备
    public static final int STATE_DISCONNECT = 0xCC;      // 设备断开

    public static final int BUFF_LEN = 1024 * 4;

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothAdapter locationAdapter; // 本地蓝牙适配器
    private BluetoothDevice connectDevice;

    private List<BleConnectionObserver> bleConnectionObservers = new ArrayList<>();
    private List<BleScanObserver> onBleScanObservers = new ArrayList<>();
    private List<BleReceiveObserver> bleReceiveObservers = new ArrayList<>();

    private int deviceState = STATE_NONE;   // 设备状态
    private boolean isReceive = true;
    private Context context;
    private ExecutorService singleThreadPool;
    private Handler handler = new Handler();
    private IBleParser bleParser;

    private static BleManager instance;
    private BleManager() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();
        singleThreadPool = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static final BleManager getInstance() {
        if(instance == null) {
            synchronized (BleManager.class) {
                instance = new BleManager();
            }
        }
        return instance;
    }

    public void init(Context context, IBleParser bleParser) {
        this.context = context;
        this.bleParser = bleParser;
        socket = null;
        locationAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        context.registerReceiver(mReceiver, filter);

    }

    /**
     * 判断蓝牙是否打开
     */
    public boolean isEnableBluetooth() {
        return locationAdapter.isEnabled();
    }

    /**
     * 开启蓝牙设备
     */
    public boolean enableBluetooth() {
        return locationAdapter.enable();
    }

    /**
     * 关闭蓝牙设备
     */
    public boolean disableBluetooth() {
        return locationAdapter.disable();
    }

    /**
     * 获取已配对的设备列表
     */
    public List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> deviceList = new ArrayList<>();
        Set<BluetoothDevice> deviceSet = locationAdapter.getBondedDevices();
        deviceList.addAll(deviceSet);
        return deviceList;
    }

    /**
     * 开始搜索设备
     */
    public void startDiscovery() {
        cancelDiscovery();
        locationAdapter.startDiscovery();
    }

    /**
     * 取消搜索设备
     */
    public void cancelDiscovery() {
        if (locationAdapter.isDiscovering()) {
            locationAdapter.cancelDiscovery();
        }
    }

    public boolean isConnected() {
        return socket != null;
    }

    public void disConnect() {
        connectDevice = null;
        if (socket != null) {
            try {
                isReceive = false;
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (outputStream != null) {
                    outputStream.close();
                    outputStream = null;
                }
                if (socket != null && socket.isConnected()) {
                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inputStream = null;
                outputStream = null;
                socket = null;
            }
        }
        deviceState = STATE_DISCONNECT;
    }

    public boolean isConnecting() {
        return deviceState == STATE_CONNECTING;
    }

    public int getDeviceState() {
        return deviceState;
    }

    public void connect(String addr) {
        connect(locationAdapter.getRemoteDevice(addr));
    }

    public void connect(BluetoothDevice remoteDevice) {
        if (deviceState == STATE_CONNECTED || deviceState == STATE_CONNECTING || remoteDevice == null) {
            return;
        }
        for(BleConnectionObserver listener : bleConnectionObservers) {
            listener.onStartConnect();
        }
        cancelDiscovery();
        disConnect();
        singleThreadPool.execute(new ConnectRunnable(remoteDevice));
    }


    public void send(byte... bytes) {
        singleThreadPool.execute(new SendRunnable(bytes));
    }


    class ConnectRunnable implements Runnable {

        BluetoothDevice remoteDevice;

        public ConnectRunnable(BluetoothDevice remoteDevice) {
            this.remoteDevice = remoteDevice;
        }

        @Override
        public void run() {
            try {
                socket = remoteDevice.createRfcommSocketToServiceRecord(UUID_COM);
                if (socket != null) {
                    deviceState = STATE_CONNECTING;
                    LogUtils.e("connect start");
                    socket.connect();
                    LogUtils.e("connect over");
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                e.printStackTrace();
                deviceState = STATE_DISCONNECT;
                disConnect();
                connectDevice = null;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for(BleConnectionObserver listener : bleConnectionObservers) {
                            listener.onConnectFailed(remoteDevice);
                        }
                    }
                });
            }

            if (socket != null && inputStream != null) {
                try {
                    connectDevice = remoteDevice;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for(BleConnectionObserver listener : bleConnectionObservers) {
                                listener.onConnectSuccess(connectDevice);
                            }
                        }
                    });

                    final byte[] buf = new byte[BUFF_LEN];
                    int recvLen = 0;
                    int lenCount = 0;
                    isReceive = true;
                    while (isReceive && inputStream != null) {
                        recvLen = inputStream.read(buf, lenCount, BUFF_LEN - lenCount);
                        if(recvLen > 0) {
                            lenCount = bleParser.doParse(buf, lenCount + recvLen, new BleCallBack() {
                                @Override
                                public void callBack(final BaseBleMsgEntity data) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (BleReceiveObserver observer : bleReceiveObservers) {
                                                observer.onReceive(data);
                                            }
                                        }
                                    });
                                }
                            });
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    deviceState = STATE_DISCONNECT;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for(BleConnectionObserver listener : bleConnectionObservers) {
                                listener.onDisConnected(remoteDevice);
                            }
                        }
                    });

                }
                disConnect();
            }
        }
    }


    class SendRunnable implements Runnable {

        byte[] bytes;

        public SendRunnable(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            if (outputStream != null) {
                synchronized (outputStream) {
                    try {
                        outputStream.write(bytes);
                        outputStream.flush();
                        Log.i(TAG, "Send " + bytes.length + " bytes Over");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public BluetoothDevice getConnectDevice() {
        if (socket == null) {
            return null;
        }
        return connectDevice;
    }

    private final BroadcastReceiver mReceiver = new BleReceiver();

    class BleReceiver extends BroadcastReceiver {
        private boolean adapterIsEnable;

        public BleReceiver() {
            adapterIsEnable = BluetoothAdapter.getDefaultAdapter().isEnabled();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    // 开始搜索设备
                    for(BleScanObserver listener : onBleScanObservers) {
                        listener.onScanStart();
                    }
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    // 搜索到设备
                    for(BleScanObserver listener : onBleScanObservers) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        listener.onScanDevice(device);
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    // 结束搜索
                    for(BleScanObserver listener : onBleScanObservers) {
                        listener.onScanFinished();
                    }
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Log.i(TAG, "locationAdapter.isEnabled:" + locationAdapter.isEnabled());
                    adapterIsEnable = locationAdapter.isEnabled();
                    break;


                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.i(TAG, "ACTION_BOND_STATE_CHANGED");
                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    // 已连接到设备
                    Log.i(TAG, "ACTION_ACL_CONNECTED");
                    deviceState = STATE_CONNECTED;
//                    connectDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    Log.i(TAG, "ACTION_ACL_DISCONNECT_REQUESTED");
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    // 设备断开连接
                    Log.i(TAG, "ACTION_ACL_DISCONNECTED");
                    deviceState = STATE_DISCONNECT;
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    for(OnBleConnectionObserver listener : bleConnectionObservers) {
//                        listener.onDisConnected(device);
//                    }
                    break;

                default:
                    break;
            }

        }
    };

    public void release() {
        context.unregisterReceiver(mReceiver);
        disConnect();
        if(singleThreadPool != null) {
            singleThreadPool.shutdown();
        }
    }


    public void registerBleConnectionObserver(boolean isRegister, BleConnectionObserver onBleConnectionObserver) {
        if(onBleConnectionObserver == null) {
            return;
        }
        if(isRegister && !bleConnectionObservers.contains(onBleConnectionObserver)) {
                bleConnectionObservers.add(onBleConnectionObserver);
        }
        if(!isRegister && bleConnectionObservers.contains(onBleConnectionObserver)) {
            bleConnectionObservers.remove(onBleConnectionObserver);
        }
    }

    public void registerBleScanObserver(boolean isRegister, BleScanObserver onBleScanObserver) {
        if(onBleScanObserver == null) {
            return;
        }

        if(isRegister && !onBleScanObservers.contains(onBleScanObserver)) {
            onBleScanObservers.add(onBleScanObserver);
        }

        if(!isRegister && onBleScanObservers.contains(onBleScanObserver)) {
            onBleScanObservers.remove(onBleScanObserver);
        }
    }

    public void registerBleReceiveObserver(boolean isRegister, BleReceiveObserver onBleReceiveObserver) {
        if(onBleReceiveObserver == null) {
            return;
        }

        if(isRegister && !bleReceiveObservers.contains(onBleReceiveObserver)) {
            bleReceiveObservers.add(onBleReceiveObserver);
        }

        if(!isRegister && bleReceiveObservers.contains(onBleReceiveObserver)) {
            bleReceiveObservers.remove(onBleReceiveObserver);
        }
    }
}
