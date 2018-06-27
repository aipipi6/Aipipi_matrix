package com.example.aipipi.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.aipipi.R;
import com.example.aipipi.activity.MainActivity;
import com.example.aipipi.ble.entity.BaseBleMsgEntity;
import com.example.aipipi.ble.observer.BleReceiveObserver;
import com.example.aipipi.protocol.Protocol;
import com.example.aipipi.protocol.UpdateAckMsg;
import com.freelink.library.dialog.BaseNormalDialog;
import com.freelink.library.widget.CircularProgressView;

import java.util.List;

/**
 * Created by chenjun on 2018/6/27.
 */

public class UpdateFontDialog extends BaseNormalDialog {

    private static final String TAG = UpdateFontDialog.class.getSimpleName();
    private CircularProgressView circularProgressView;
    private List<byte[]> fontList;

    public UpdateFontDialog(@NonNull Context context, @NonNull List<byte[]> fontList) {
        super(context);
        this.fontList = fontList;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_update_font;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        circularProgressView = findViewById(R.id.CircularProgressView);

        MainActivity.sBleService.registerBleReceiveObserver(true, bleReceiveObserver);

        sendFontMsg(0);
    }

    BleReceiveObserver bleReceiveObserver = new BleReceiveObserver() {
        @Override
        public void onReceive(BaseBleMsgEntity bleMsgEntity) {
            if(bleMsgEntity instanceof UpdateAckMsg) {
                UpdateAckMsg updateAckMsg = (UpdateAckMsg) bleMsgEntity;
                int index = updateAckMsg.getIndex();
                circularProgressView.setProgress(index / (float)fontList.size());
                if(index < fontList.size()) {
                    Log.i(TAG, "update: " + index + ", total:" + fontList.size());
                    sendFontMsg(index);
                } else {
                    Log.i(TAG, "update font success");
                    dismiss();
                }
            }
        }
    };

    private void sendFontMsg(int index) {
        if(index >= fontList.size()) {
            return ;
        }
        byte[] font = fontList.get(index);
        byte[] fontBytes = Protocol.newBytes(Protocol.CMD_UPTATE_FONT, 2 + font.length);
        int startIndex = Protocol.HEADER_LEN;
        fontBytes[startIndex] = (byte)(index & 0xFF);
        fontBytes[startIndex + 1] = (byte)(fontList.size() & 0xFF);

        System.arraycopy(font, 0, fontBytes, startIndex + 2, font.length);
        Protocol.setCheckSum(fontBytes);

        MainActivity.sBleService.send(fontBytes);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        MainActivity.sBleService.registerBleReceiveObserver(false, bleReceiveObserver);
    }

}
