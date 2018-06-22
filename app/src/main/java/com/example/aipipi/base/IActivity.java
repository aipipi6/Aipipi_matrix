package com.example.aipipi.base;

/**
 * Created by chenjun on 2017/12/31.
 */

public interface IActivity {
    void hideLoadingDialog();
    void showLoadingDialog(String content);
    void showKeyboard(boolean isShow);
}
