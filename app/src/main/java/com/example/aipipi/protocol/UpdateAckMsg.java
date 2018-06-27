package com.example.aipipi.protocol;

import com.example.aipipi.ble.entity.BaseBleMsgEntity;

/**
 * Created by chenjun on 2018/6/27.
 */

public class UpdateAckMsg extends BaseBleMsgEntity {

    public UpdateAckMsg() {
        super(Protocol.ACK_UPTATE_FONT);
    }

    private int index;

    @Override
    public void convert(int start, int len, byte[] datas) {
        index = datas[start];
    }

    public int getIndex() {
        return index;
    }
}
