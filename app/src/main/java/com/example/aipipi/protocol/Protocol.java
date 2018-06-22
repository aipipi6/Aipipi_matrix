package com.example.aipipi.protocol;

/**
 * Created by chenjun on 2018/6/21.
 */

public class Protocol {

    public static final byte START_TAG1 = (byte) 0x90;
    public static final byte START_TAG2 = (byte) 0x96;
    public static final int HEADER_LEN = 5;
    public static final int CKSUM_LEN = 1;

    public static final byte CMD_FONT = (byte) 0x01;


    public static byte[] newBytes(byte cmd, int len) {
        byte[] bytes = new byte[HEADER_LEN + len + CKSUM_LEN];
        bytes[0] = START_TAG1;
        bytes[1] = START_TAG2;
        bytes[2] = cmd;
        bytes[3] =  (byte) ((len >> 8) & 0xFF);
        bytes[4] =  (byte) ((len     ) & 0xFF);

        return bytes;
    }

    public static void setCheckSum(byte[] bytes) {
        byte ckSum = calcCheckSum(0, bytes.length - 1, bytes);
        bytes[bytes.length - 1] = ckSum;
    }

    public static byte calcCheckSum(int start, int len, byte[] bytes) {
        byte ckSum = 0;
        for(int i = 0; i < len; i++) {
            ckSum ^= bytes[i + start];
        }
        return ckSum;
    }

}
