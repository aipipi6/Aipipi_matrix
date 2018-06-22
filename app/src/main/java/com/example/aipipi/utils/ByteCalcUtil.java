package com.example.aipipi.utils;

import java.util.List;


public class ByteCalcUtil {


    public static int get1Bit(int index, int data) {
        return (data >>> index) & 0x1;
    }

    /**
     * 取一个字节
     * @param i     起始位
     * @param data
     * @return
     */
    public static byte getByte(int i, int data) {
        return (byte) ((data >>> i) & 0xFF);
    }
    public static byte getByte(int i, long data) {
        return (byte) ((data >>> i) & 0xFF);
    }

    public static byte getByte(int data) {
        return (byte) (data & 0xFF);
    }


    /**
     * 从数组中取1个字节的数据
     * @param start
     * @param datas
     * @return
     */
    public static byte getS8FromArray(int start, byte[] datas) {
        return (byte) getU8FromArray(start, datas);
    }

    public static int getU8FromArray(int start, byte[] datas) {
        if(start < datas.length) {
            return datas[start] & 0xFF;
        } else {
            throw new IllegalArgumentException("out of bound of array");
        }
    }

    /**
     * 从数组中取2个字节的数据
     * @param start
     * @param datas
     * @return
     */
    public static short getS16FromArray(int start, byte[] datas) {
        return (short) getU16FromArray(start, datas);
    }

    public static int getU16FromArray(int start, byte[] datas) {
        if (start + 1 < datas.length) {
            int i = datas[start] & 0xFF;
            int j = datas[start + 1] & 0xFF;
            return (i << 8 | j);
        } else {
            throw new IllegalArgumentException("out of bound of array");
        }
    }

    /**
     * 从数组中取4个字节的数据
     * @param start
     * @param datas
     * @return
     */
    public static int getS32FromArray(int start, byte[] datas) {
        return (int) getU32FromArray(start, datas);
    }

    public static int getS32FromArrayLittle(int start, byte[] datas) {
        if (start + 3 < datas.length) {
            int i = datas[start + 3] & 0xFF;
            int j = datas[start + 2] & 0xFF;
            int k = datas[start + 1] & 0xFF;
            int l = datas[start + 0] & 0xFF;
            return (i << 24 | j << 16 | k << 8 | l);
        } else {
            throw new IllegalArgumentException("out of bound of array");
        }
    }

    public static long getU32FromArray(int start, byte[] datas) {
        if (start + 3 < datas.length) {
            int i = datas[start] & 0xFF;
            int j = datas[start + 1] & 0xFF;
            int k = datas[start + 2] & 0xFF;
            int l = datas[start + 3] & 0xFF;
            return (i << 24 | j << 16 | k << 8 | l);
        } else {
            throw new IllegalArgumentException("out of bound of array");
        }
    }

    public static int getAnyByteFromArray(int size, int start, byte[] datas) {
        if(size == 1) {
            return datas[start];
        } else if(size == 2) {
            return datas[start] << 8 | datas[start + 1];
        } else if(size == 4) {
            return  datas[start] << 24 | datas[start + 1] << 16
                    | datas[start + 2] << 8 | datas[start + 3];
        } else {
            throw new IllegalArgumentException("The param(size) have to be 1, 2, 4");
        }
    }

    /**
     * 从数组中截取取一段数组
     */
    public static byte[] getBytes(int start, byte[] datas) {
        return getBytes(start, datas.length - start, datas);
    }
    public static byte[] getBytes(int start, int len, byte[] datas) {
        if(start + len > datas.length) {
            len = datas.length - start;
        }
        byte[] newDatas = new byte[len];
        System.arraycopy(datas, start, newDatas, 0, len);
        return newDatas;

    }

    public static byte[] getBytes(int start, int len, List<Byte> datas) {
        if(start + len > datas.size()) {
            len = datas.size() - start;
        }
        byte[] newDatas = new byte[len];

        for(int i = 0; i < len; i++) {
            newDatas[i] = datas.get(i + start);
        }
        return newDatas;

    }

    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        return b;
/*

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;
*/

    }

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param start 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int start) {
        int l;
        l = b[start + 3];
        l &= 0xff;
        l |= ((long) b[start + 2] << 8);
        l &= 0xffff;
        l |= ((long) b[start + 1] << 16);
        l &= 0xffffff;
        l |= ((long) b[start + 0] << 24);

        return Float.intBitsToFloat(l);
    }
    public static float byte2float(byte[] b) {
        return byte2float(b, 0);
    }

    /**
     * 将byte数组转换成字符串
     * @param array
     * @return
     */
    public static String byteArrayToString(byte[] array) {
        return byteArrayToString(0, array.length, array);
    }
    public static String byteArrayToString(int len, byte[] array) {
        return byteArrayToString(0, len, array);
    }

    public static String byteArrayToString(int start, int len, byte[] array) {
        StringBuffer sb = new StringBuffer();
        len = array.length < len ? array.length : len;
        for(int i = 0; i < len; i++) {
            sb.append(String.format("%02x", array[start + i]));
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * 从数组中根据数据类型获取数值
     */
    public static String getValueByType(String type, byte[] array) {
        return getValueByType(type, 0, array);
    }

    public static String getValueByType(String type, int start, byte[] array) {
        if("s8".equalsIgnoreCase(type)) {
            int v  = ByteCalcUtil.getS8FromArray(start, array);
            return v + "";
        } else if("u8".equalsIgnoreCase(type)) {
            int v = ByteCalcUtil.getU8FromArray(start, array);
            return  v + "";
        } else if("s16".equalsIgnoreCase(type)) {
            short v = ByteCalcUtil.getS16FromArray(start, array);
            return  v + "";
        } else if("u16".equalsIgnoreCase(type)) {
            int v = ByteCalcUtil.getU16FromArray(start, array);
            return  v + "";
        } else if("u32".equalsIgnoreCase(type)) {
            long v = ByteCalcUtil.getU32FromArray(start, array);
            return  v + "";
        } else if("float".equalsIgnoreCase(type)) {
            float v = ByteCalcUtil.byte2float(array, start);
            return StringUtil.float2String(v);
        } else {
            return "";
        }
    }

}
