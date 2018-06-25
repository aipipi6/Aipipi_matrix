package com.example.aipipi.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.freelink.library.dialog.LoadingDialog;


public class BaseActivity extends AppCompatActivity implements IActivity{

    protected Context mContext;
    protected AppCompatActivity mActivity;
    private LoadingDialog loadingDialog;

    private boolean destroyed = false;
    private static Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenAdaptation.setCustomDensity(this, getApplication());
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
    }

    /**
     * 显示加载进度dialog
     */

    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    @Override
    public void showLoadingDialog(String content) {
        showLoadingDialog(content, 0, null);
    }

    public void showLoadingDialog(String content, DialogInterface.OnCancelListener onCancelListener) {
        showLoadingDialog(content, 0, onCancelListener);
    }

    public void showLoadingDialog(String content, int totalTime) {
        showLoadingDialog(content, totalTime, null);
    }

    public void showLoadingDialog(int totalTime) {
        showLoadingDialog(null, totalTime, null);
    }

    public void showLoadingDialog(String content, int totalTime,  DialogInterface.OnCancelListener onCancelListener) {
        if(StringUtils.isEmpty(content)) {
            content = "加载中";
        }
        if(loadingDialog == null) {
            loadingDialog = new LoadingDialog(mContext, content, totalTime);
            loadingDialog.show();
        } else {
            loadingDialog.setContentText(content);
        }

        if(onCancelListener != null) {
            loadingDialog.setOnCancelListener(onCancelListener);
            loadingDialog.setCanceledOnTouchOutside(true);
        }
    }


    /**
     * 隐藏加载进度dialog
     */
    @Override
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }
    }

    public void showToast(String msg) {
        ToastUtils.showShort(msg);
    }

    public void showToast(@StringRes int stringRes) {
        ToastUtils.showShort(stringRes);
    }

    @Override
    public void showKeyboard(boolean isShow) {
        if(isShow) {
            KeyboardUtils.showSoftInput(this);
        } else {
            KeyboardUtils.hideSoftInput(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    /**
     * 延时弹出键盘
     *
     * @param focus ：键盘的焦点项
     */
    protected void showKeyboardDelayed(View focus) {
        final View viewToFocus = focus;
        if (focus != null) {
            focus.requestFocus();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (viewToFocus == null || viewToFocus.isFocused()) {
                    showKeyboard(true);
                }
            }
        }, 200);
    }

    /**
     * 判断页面是否已经被销毁（异步回调时使用）
     */
    protected boolean isDestroyedCompatible() {
        if (Build.VERSION.SDK_INT >= 17) {
            return isDestroyedCompatible17();
        } else {
            return destroyed || super.isFinishing();
        }
    }

    @TargetApi(17)
    private boolean isDestroyedCompatible17() {
        return super.isDestroyed();
    }

    protected final Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(getMainLooper());
        }
        return mHandler;
    }

    protected <T extends View> T findView(int resId) {
        return (T) (findViewById(resId));
    }
    protected void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    protected void startActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<? extends Activity> activityClass, int requestCode) {
        Intent intent = new Intent(this, activityClass);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<? extends Activity> activityClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    protected int getDimensionPixelSize(int size) {
        return getResources().getDimensionPixelSize(size);
    }

    protected int getColorFromRes(@ColorRes int colorRes) {
        return ContextCompat.getColor(mContext, colorRes);
    }

    protected final int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
