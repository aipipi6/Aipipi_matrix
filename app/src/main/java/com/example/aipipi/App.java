package com.example.aipipi;

import android.app.Application;
import android.view.Gravity;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Created by chenjun on 2018/6/8.
 */

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);

        initToast();
    }

    private void initToast() {
        ToastUtils.setBgResource(R.drawable.bg_toast);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.setMsgColor(0xFFFFFFFF);
        ToastUtils.setMsgSize(SizeUtils.dp2px(15));
    }

}
