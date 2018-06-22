package com.example.aipipi.ble.parser;


public interface IBleParser {

    int doParse(byte[] datas, int len, BleCallBack callBack);
}
