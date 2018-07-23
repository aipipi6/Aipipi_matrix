package com.example.aipipi.activity;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.example.aipipi.R;
import com.example.aipipi.base.BaseCallBack;
import com.example.aipipi.base.BaseToolBarActivity;
import com.example.aipipi.ble.BleService;
import com.example.aipipi.ble.observer.BleConnectionObserver;
import com.example.aipipi.ble.observer.BleScanObserver;
import com.example.aipipi.dialog.UpdateFontDialog;
import com.example.aipipi.entity.TextFont;
import com.example.aipipi.protocol.Protocol;
import com.example.aipipi.utils.font.OnMakeFontListener;
import com.example.aipipi.widget.DotMatrixView;
import com.example.aipipi.utils.StringUtil;
import com.example.aipipi.utils.font.FontUtils;
import com.freelink.library.viewHelper.RadioGroupHelper;
import com.freelink.library.widget.ImageTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseToolBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DEFAULE_FONT_SIZE = FontUtils.FONT_SIZE_24;
//    private static final String DEFAULT_BLE_DEVICE_ADDR = "98:D3:31:80:1E:9D";
    private static final String DEFAULT_BLE_DEVICE_ADDR = "98:D3:37:00:B6:2E";

    @BindView(R.id.itv_ble)
    ImageTextView itvBle;

    @BindView(R.id.ll_font)
    LinearLayout llFont;

    @BindView(R.id.ll_color)
    LinearLayout llColor;

    @BindView(R.id.et_text)
    EditText editText;

    @BindView(R.id.tv_text_count)
    TextView tvTextCount;

    @BindView(R.id.multiDotMatrixView)
    DotMatrixView dotMatrixView;

    private RadioGroupHelper fontRadioGroupHelper;
    private RadioGroupHelper colorRadioGroupHelper;

    final static String[] sFontTyps = {"宋体", "黑体", "仿宋", "楷体"};
    private TextFont textFont;

    public static BleService sBleService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, bleServiceConnection, BIND_AUTO_CREATE);

        showToolBar(false);

        fontRadioGroupHelper = new RadioGroupHelper(llFont, 0);

        fontRadioGroupHelper.setOnCheckedChangeListener(new RadioGroupHelper.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View v, int index) {
//                LogUtils.e(index + "/" + fontRadioGroupHelper.getCheckedRadioIndex());
            }
        });

        dotMatrixView.setDotColor(getColorFromRes(R.color.red));
        colorRadioGroupHelper = new RadioGroupHelper(llColor, 0);
        colorRadioGroupHelper.setOnCheckedChangeListener(new RadioGroupHelper.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View v, int index) {
                if(index == 0) {
                    dotMatrixView.setDotColor(getColorFromRes(R.color.red));
                } else {
                    dotMatrixView.setDotColor(getColorFromRes(R.color.blue));
                }
                if(sBleService.isConnected()) {
                    byte[] colorBytes = Protocol.newBytes(Protocol.CMD_UPDATE_COLOR, 1);
                    int startIndex = Protocol.HEADER_LEN;
                    colorBytes[startIndex] = (byte) (index & 0xFF);
                    Protocol.setCheckSum(colorBytes);
                    MainActivity.sBleService.send(colorBytes);
                } else {
                    showToast("请先连接蓝牙设备");
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.toString().length();
                tvTextCount.setText(count + "/" + 120);
            }
        });
    }

    ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleService.InnerBinder binder = (BleService.InnerBinder) service;
            sBleService = binder.getService();
            sBleService.registerBleScanObserver(true, bleScanObserver);
            sBleService.registerBleConnectionObserver(true, bleConnectionObserver);
            if(sBleService.isConnected()) {
                itvBle.setText(R.string.ble_connected);
                itvBle.setSelected(true);
            } else {
                itvBle.setText(R.string.ble_disconnect);
                sBleService.startDiscovery();
                itvBle.setSelected(false);
            }
            LogUtils.e("bleServiceConnection");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    BleScanObserver bleScanObserver = new BleScanObserver() {
        @Override
        public void onScanStart() {
            showLoadingDialog("搜索设备中", new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if(!sBleService.isConnected()
                            && !sBleService.isConnecting()) {
                        showToast("未搜索到设备");
                    }
                    sBleService.cancelDiscovery();
                }
            });
        }

        @Override
        public void onScanDevice(BluetoothDevice device) {
            if(device.getAddress().equalsIgnoreCase(DEFAULT_BLE_DEVICE_ADDR)) {
                sBleService.connect(device);
            }
        }

        @Override
        public void onScanFinished() {
            if(!sBleService.isConnected()
                    && !sBleService.isConnecting()) {
                hideLoadingDialog();
                showToast("未搜索到设备");
            }
        }
    };

    BleConnectionObserver bleConnectionObserver = new BleConnectionObserver() {
        @Override
        public void onStartConnect() {
            showLoadingDialog("连接设备中");
        }

        @Override
        public void onConnectSuccess(BluetoothDevice device) {
            hideLoadingDialog();
            showToast("连接设备成功");
            itvBle.setText(R.string.ble_connected);
            itvBle.setSelected(true);
        }

        @Override
        public void onConnectFailed(BluetoothDevice device) {
            hideLoadingDialog();
            showToast("连接设备失败");
            itvBle.setText(R.string.ble_disconnect);
            itvBle.setSelected(false);
        }

        @Override
        public void onDisConnected(BluetoothDevice device) {
            hideLoadingDialog();
            showToast("设备断开连接");
            itvBle.setText(R.string.ble_disconnect);
            itvBle.setSelected(false);
        }
    };

    @OnClick(R.id.tv_preview)
    void onPreview() {
        resetTextFont();
        makeFont(new BaseCallBack<List<byte[]>>() {
            @Override
            public void onCallBack(List<byte[]> obj) {
                dotMatrixView.setMatrix(FontUtils.convertMatrix(textFont.getFontSize(), textFont.getFontList()));
                dotMatrixView.startScroll(100);
            }
        });
    }

    @OnClick(R.id.tv_send)
    void onSendFont() {
        if(sBleService.isConnected()) {
            resetTextFont();
            makeFont(new BaseCallBack<List<byte[]>>() {
                @Override
                public void onCallBack(List<byte[]> fontList) {
                    UpdateFontDialog updateFontDialog = new UpdateFontDialog(context, fontList);
                    updateFontDialog.show();
                    updateFontDialog.startUpdate();
                }
            });
        } else {
            showToast("请先连接蓝牙设备");
        }
    }

    @OnClick(R.id.itv_ble)
    void onConnectBle() {
        if(!sBleService.isConnected()
                && !sBleService.isConnecting()) {
            sBleService.startDiscovery();
        } else {
            sBleService.disConnect();
        }
    }

    private void resetTextFont() {
        if(textFont == null) {
            textFont = new TextFont();
        }

        String text = editText.getText().toString();
        if(StringUtil.isEmpty(text)) {
            showToast("请输入文本");
            return;
        }
        int index = fontRadioGroupHelper.getCheckedRadioIndex();
        final String fontType = sFontTyps[index];

        textFont.setText(text);
        textFont.setFontSize(DEFAULE_FONT_SIZE);
        textFont.setFontType(fontType);
    }


    private void makeFont(final BaseCallBack<List<byte[]>> callBack) {
        if(textFont.getText().length() > 10) {
            showLoadingDialog("生成字库中");
        }
        new AsyncTask<Void, Integer, List<byte[]>>() {
            @Override
            protected List<byte[]> doInBackground(Void... params) {

                return  FontUtils.makeFont(textFont.getFontSize(), textFont.getFontType(), textFont.getText(), new OnMakeFontListener() {
                    @Override
                    public void schedule(int current, int total) {
                        publishProgress(current, total);
                    }
                });
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int current = values[0];
                int total = values[1];
                float progress = current / (float) total;
            }

            @Override
            protected void onPostExecute(List<byte[]> bytes) {
                textFont.setFontList(bytes);
                hideLoadingDialog();
                if(callBack != null) {
                    callBack.onCallBack(bytes);
                }
            }
        }.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dotMatrixView.stopScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dotMatrixView.resumeScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sBleService != null) {
            sBleService.registerBleScanObserver(false, bleScanObserver);
            sBleService.registerBleConnectionObserver(false, bleConnectionObserver);
            sBleService = null;
        }
    }


    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            // 2s内连续点击back两次可退出程序
            super.onBackPressed();
        }
    }

}
