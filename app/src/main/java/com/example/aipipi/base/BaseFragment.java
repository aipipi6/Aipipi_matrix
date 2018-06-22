package com.example.aipipi.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.freelink.library.dialog.LoadingDialog;


public class BaseFragment extends Fragment {
    protected Context mContext;
    protected AppCompatActivity mActivity;
    private LoadingDialog loadingDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = (AppCompatActivity) getActivity();
    }

    protected void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        startActivity(intent);
    }

    protected void startActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(getActivity(), activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 显示加载进度dialog
     *
     * @param content
     */
    public void showLoadingDialog(String content) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setTitle(content);
        } else {
            hideLoadingDialog();
            loadingDialog = new LoadingDialog(mContext, content);
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载进度dialog
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }
    }

    protected int getDimensionPixelSize(int size) {
        return getResources().getDimensionPixelSize(size);
    }
}
