package com.example.aipipi.utils;


import com.blankj.utilcode.util.StringUtils;

/**
 * Created by chenjun on 2017/12/5.
 */

public class StringUtil extends StringUtils {


    private StringUtil() {
        super();
    }

    public static String double2String(double d) {
        return d == (int)d ? String.valueOf((int)d) : String.valueOf(d);
    }

    public static String float2String(float d) {
        return d == (int)d ? String.valueOf((int)d) : String.valueOf(d);
    }

    private static final String[] hexTextArray =
            {"0", "1", "2", "3", "4", "5", "6", "7", "8","9", "A", "B", "C", "D", "E", "F"};
    public static String byteArray2hexString(byte[] bytes) {
        return byteArray2hexString(0, bytes.length, bytes);
    }
    public static String byteArray2hexString(int start, int len, byte[] bytes) {
        if(start + len <= bytes.length) {
            StringBuffer stringBuffer = new StringBuffer();
            for(byte b : bytes) {
                int b1 = (b >> 4) & 0xF;
                int b2 = b & 0xF;
                stringBuffer.append(hexTextArray[b1]);
                stringBuffer.append(hexTextArray[b2]);
                stringBuffer.append(' ');
            }
            return stringBuffer.toString();
        } else {
            throw new IllegalArgumentException("out of bound of array");
        }
    }
}
