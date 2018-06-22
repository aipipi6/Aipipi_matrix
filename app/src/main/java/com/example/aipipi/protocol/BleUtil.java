package com.example.aipipi.protocol;

import com.example.aipipi.ble.BleManager;

import java.util.List;

/**
 * Created by chenjun on 2018/6/21.
 */

public class BleUtil {


    public static void sendFontMsg(int index, List<byte[]> fontList) {
        if(index >= fontList.size()) {
            return ;
        }
        int offset = 0;
        for(int i = 0; i < index; i++) {
            byte[] font = fontList.get(i);
            offset += font.length;
        }

        byte[] font = fontList.get(index);
        byte[] fontBytes = Protocol.newBytes(Protocol.CMD_FONT, 2 + font.length);
        int startIndex = Protocol.HEADER_LEN;
        fontBytes[startIndex] = (byte)((offset >> 8) & 0xFF);
        fontBytes[startIndex + 1] = (byte)(offset & 0xFF);
        System.arraycopy(font, 0, fontBytes, startIndex + 2, font.length);
        Protocol.setCheckSum(fontBytes);
        BleManager.getInstance().send(fontBytes);
    }
}
