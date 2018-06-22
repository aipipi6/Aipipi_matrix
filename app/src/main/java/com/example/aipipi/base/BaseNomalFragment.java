package com.example.aipipi.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.freelink.library.dialog.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseNomalFragment<P extends BasePresenter> extends Fragment {

    protected Context mContext;
    protected AppCompatActivity activity;
    protected View rootView;

    private LoadingDialog loadingDialog;
    protected P mPresenter;
    protected Unbinder unbinder;
    protected Handler mHandler = new Handler();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
        this.activity = (AppCompatActivity) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = newPresenter();
        if(mPresenter != null) {
            mPresenter.attach(this);
        }
        initData(savedInstanceState);
        initView();
    }

    protected abstract int getLayoutId();
    protected abstract void initData(Bundle savedInstanceState);
    protected void initView() {}
    protected Class<P> getPresenterClass() {
        return null;
    }

    private P newPresenter() {
        Class<P> clz = getPresenterClass();
        if(clz != null) {
            try {
                return clz.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected <V extends View> V findViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(unbinder != null || unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
            unbinder = null;
        }
        if(mPresenter != null) {
            mPresenter.dettach();
        }
    }

    /**
     * 显示加载进度dialog
     * @param content
     */
    public void showLoadingDialog(String content) {
        showLoadingDialog(content, false);
    }
    public void showLoadingDialog(String content, boolean cancel) {
        if(loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setTitle(content);
        } else {
            hideLoadingDialog();
            loadingDialog = new LoadingDialog(getActivity(), content);
            loadingDialog.setCanceledOnTouchOutside(cancel);
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载进度dialog
     */
    public void hideLoadingDialog() {
        if(loadingDialog != null) {
            if(loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }
    }


    public void showToast(String msg) {
        ToastUtils.showShort(msg);
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


    protected int getDimensionPixelSize(int size) {
        return getResources().getDimensionPixelSize(size);
    }

    protected int getColorFromRes(@ColorRes int res) {
        return ContextCompat.getColor(mContext, res);
    }

    protected final int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
