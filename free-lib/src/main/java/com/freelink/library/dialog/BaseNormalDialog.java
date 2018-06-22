package com.freelink.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.freelink.library.R;


public abstract class BaseNormalDialog extends Dialog {
    protected Context context;
    protected View rootView;
    protected Handler handler = new Handler();

    public BaseNormalDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);// android:windowNoTitle
        rootView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        setContentView(rootView, getContentLayoutParams());
        getWindow().setGravity(getGravity());
        getWindow().setBackgroundDrawableResource(R.color.transparent);// android:windowBackground
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);// android:backgroundDimEnabled默认是true的
        if(getWindowAnimations() > 0) {
            getWindow().setWindowAnimations(getWindowAnimations());
        }
        getWindow().setDimAmount(getDimAmount());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    protected abstract int getLayoutId();

    protected int getWindowAnimations() {
        // R.style.FromBottomAnimation
        return  -1;
    }

    protected ViewGroup.LayoutParams getContentLayoutParams() {
        return new ViewGroup.LayoutParams(context.getResources().getDisplayMetrics().widthPixels,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * 遮罩透明度
     */
    protected float getDimAmount() {
        return 0.5f;
    }

    /**
     * dialog所处位置
     */
    protected int getGravity() {
        return Gravity.CENTER;
    }


    protected int getColorFromRes(@ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    protected int dp2px(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
