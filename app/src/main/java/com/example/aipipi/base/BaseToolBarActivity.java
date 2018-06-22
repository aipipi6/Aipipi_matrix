package com.example.aipipi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.aipipi.R;
import com.freelink.library.widget.ImageTextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseToolBarActivity<P extends BasePresenter> extends BaseActivity {
    private View toolbar;
    private View line;
    private ImageTextView itvBack;
    private TextView tvTitle;
    private TextView tvLeft;
    private TextView tvRight;
    protected View contentView;
    private LinearLayout llRight;
    private FrameLayout flContent;
    protected Context context;
    protected AppCompatActivity activity;
    protected P mPresenter;
    protected Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_toolbar);
        context = this;
        activity = this;
        toolbar = findViewById(R.id.rl_toolbar);
        line = findViewById(R.id.line);
        itvBack = (ImageTextView) findViewById(R.id.itv_base_back);
        tvTitle = (TextView) findViewById(R.id.tv_base_title);
        tvLeft = (TextView) findViewById(R.id.tv_base_left);
        llRight = (LinearLayout) findViewById(R.id.ll_base_right);
        tvRight = (TextView) findViewById(R.id.tv_base_right);
        flContent = (FrameLayout) findViewById(R.id.fl_base_content);
        itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        itvBack.setVisibility(isShowBackIcon() ? View.VISIBLE : View.GONE);

        int layoutId = getLayoutId();
        if (layoutId > 0) {
            contentView = LayoutInflater.from(this).inflate(layoutId, null);
            unbinder = ButterKnife.bind(this, contentView);
            flContent.addView(contentView);
        }

        mPresenter = newPresenter();
        if (mPresenter != null) {
            mPresenter.attach(this);
        }
        initData(savedInstanceState);

//        ivRight.setVisibility(View.VISIBLE);
//        ivRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(BleActivity.class);
//            }
//        });
//
//        ivRight.setSelected(BleManager.getInstance().isConnected());
//        BleManager.getInstance().registerBleConnectionObserver(true, onBleConnectionObserver);
    }
//
//    BleConnectionObserver onBleConnectionObserver = new BleConnectionObserver() {
//        @Override
//        public void onStartConnect() {
//        }
//
//        @Override
//        public void onConnectSuccess(BluetoothDevice device) {
//            ivRight.setSelected(true);
//        }
//
//        @Override
//        public void onConnectFailed(BluetoothDevice device) {
//            ivRight.setSelected(false);
//        }
//
//        @Override
//        public void onDisConnected(BluetoothDevice device) {
//            ivRight.setSelected(false);
//        }
//    };

    protected abstract int getLayoutId();

    protected abstract void initData(Bundle savedInstanceState);

    protected Class<P> getPresenterClass() {
        return null;
    }

    protected P newPresenter() {

        Class<P> clz = getPresenterClass();
        if(clz != null) {
            try {
                return clz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void setTitleText(String title) {
        tvTitle.setText(title);
    }

    protected void setTitleTextColor(@ColorInt int color) {
        tvTitle.setTextColor(color);
    }

    protected void setCustomView(@LayoutRes int layoutId) {
        contentView = LayoutInflater.from(this).inflate(layoutId, null);
        flContent.addView(contentView);
    }

    protected void setShowFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_base_content, fragment).commit();
    }

    /**
     * 是否显示返回图标
     */
    protected boolean isShowBackIcon() {
        return false;
    }

    protected void showBackIcon(boolean isShow) {
        itvBack.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    protected void showToolBar(boolean isShow) {
        toolbar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    protected void setBackIcon(@DrawableRes int resId) {
        itvBack.setVisibility(View.VISIBLE);
//        itvBack.setImageResource(resId);
        itvBack.setLeftDrawable(resId);
    }


    public void setBackTextColor(@ColorInt int color) {
        itvBack.setTextColor(color);
    }

    protected void setToolBarBackground(@DrawableRes int resId) {
        toolbar.setBackgroundResource(resId);
    }

    protected void setToolbarBackgroundColor(@ColorInt int color) {
        toolbar.setBackgroundColor(color);
    }

    protected void showLine(boolean isShow) {
        line.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    /**
     * 设置左边TextView 的文本
     */
    protected void setLeftText(String text) {
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setText(text);
    }

    /**
     * 设置左边TextView 的文本，并添加按键监听
     */
    protected void setLeftText(String text, View.OnClickListener onClickListener) {
        setLeftText(text);
        tvLeft.setOnClickListener(onClickListener);
    }

    /**
     * 显示或隐藏左边TextView
     */
    protected void showLeftText(boolean isShow) {
        tvLeft.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右边TextView 的文本
     */
    protected void setRightText(String text) {
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(text);
    }

    /**
     * 设置右边TextView 的文本，并添加按键监听
     */
    protected void setRightText(String text, View.OnClickListener onClickListener) {
        setRightText(text);
        tvRight.setOnClickListener(onClickListener);
    }

    /**
     * 隐藏右边TextView
     */
    protected void showRightText(boolean isShow) {
        tvRight.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    protected TextView getRightTextView() {
        return tvRight;
    }

    protected void addRightView(View view) {
        if(view != null) {
            llRight.addView(view);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.dettach();
            mPresenter = null;
        }

        if (unbinder != null && unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
            unbinder = null;
        }
    }
}
