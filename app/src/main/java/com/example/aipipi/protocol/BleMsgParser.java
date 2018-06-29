package com.example.aipipi.protocol;


import com.blankj.utilcode.util.LogUtils;
import com.example.aipipi.ble.BleManager;
import com.example.aipipi.ble.entity.BaseBleMsgEntity;
import com.example.aipipi.ble.parser.BleCallBack;
import com.example.aipipi.ble.parser.IBleParser;
import com.example.aipipi.utils.ByteCalcUtil;

public class BleMsgParser implements IBleParser {

    private final static int MAX_LEN = BleManager.BUFF_LEN;

    @Override
    public int doParse(byte[] datas, int totalLen, BleCallBack callBack) {
        if(callBack == null) {
            return 0;
        }
        int offset = 0;
        while (true) {
            int start = -1;

            for (int i = offset; i < totalLen - 1; i++) {
                if (datas[i] == Protocol.START_TAG1
                        && datas[i + 1] == Protocol.START_TAG2) {
                    start = i;
                    break;
                }
            }
            if (start == -1) {
                // 没有找到消息头标志
                if(datas[totalLen - 1] == Protocol.START_TAG1) {
                    datas[0] = Protocol.START_TAG1;
                    totalLen = 1;
                    offset = 0;
                } else {
                    totalLen = 0;
                    offset = 0;
                }
                break;
            }

            if (totalLen < start + Protocol.HEADER_LEN) {
                // 没有达到消息头的长度
                break;
            }
            int dataType = datas[start + 2];
            int dataLen = datas[start + 3];
            if (totalLen < start + Protocol.HEADER_LEN + dataLen + Protocol.CKSUM_LEN) {
                // 数据不够，继续接收
                break;
            }

            offset = start;
            // 计算校验位
            byte crc0 = datas[start + Protocol.HEADER_LEN + dataLen];
            byte crc = Protocol.calcCheckSum(start, Protocol.HEADER_LEN + dataLen, datas);
            if (crc != crc0) {
                LogUtils.e("CRC Error : read(" + crc0
                        + "),calculate(" + crc + ")");
                offset += start + Protocol.HEADER_LEN + dataLen;
                continue;
            }

            BaseBleMsgEntity entity = null;
            switch (dataType) {
                case Protocol.ACK_UPTATE_FONT:
                    entity = new UpdateAckMsg();
                    break;

                default:
                    break;
            }
            if(entity != null) {
                entity.convert(offset + Protocol.HEADER_LEN, dataLen, datas);
                callBack.callBack(entity);
            }
            offset += Protocol.HEADER_LEN + dataLen + 1;
        }
        if(offset > 0) {
            totalLen -= offset;
            for (int i = 0; i < totalLen; i++) {
                datas[i] = datas[offset + i];
            }
        }

        return totalLen;
    }



}
