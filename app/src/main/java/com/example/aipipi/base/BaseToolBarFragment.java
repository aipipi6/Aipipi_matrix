package com.example.aipipi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.aipipi.R;
import com.freelink.library.widget.ImageTextView;


public abstract class BaseToolBarFragment<P extends BasePresenter> extends BaseFragment {

    protected View contentView;

    private ImageTextView itvBack;
    private TextView tv_title;
    private TextView tv_left;
    private TextView tv_right;
    protected P mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = createPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_base_toolbar, container, false);
        itvBack = (ImageTextView) rootView.findViewById(R.id.itv_base_back);
        tv_title = (TextView) rootView.findViewById(R.id.tv_base_title);
        tv_left = (TextView) rootView.findViewById(R.id.tv_base_left);
        tv_right = (TextView) rootView.findViewById(R.id.tv_base_right);
        FrameLayout layout_content = (FrameLayout) rootView.findViewById(R.id.fl_base_content);

        contentView = inflater.inflate(getLayoutId(), null);
        layout_content.addView(contentView);
        openBack(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.attach(this);
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initData(Bundle savedInstanceState);

    protected P createPresenter() {
        return null;
    }

    protected void openBack(boolean isOpen) {
        itvBack.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
    }

    /**
     *  设置标题名称
     * @param title
     */
    protected void setTitleText(String title) {
        tv_title.setText(title);
    }

    /**
     * 设置左边TextView 的文本
     */
    protected void setLeftText(String text) {
        itvBack.setVisibility(View.GONE);
        tv_left.setVisibility(View.VISIBLE);
        tv_left.setText(text);
    }

    /**
     * 隐藏左边TextView
     */
    protected void hideLeftText() {
        tv_left.setVisibility(View.GONE);
        itvBack.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右边TextView 的文本
     */
    protected void setRightText(String text) {
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(text);
    }

    /**
     * 设置右边TextView 的文本，并添加按键监听
     */
    protected void setRightText(String text, View.OnClickListener onClickListener) {
        setRightText(text);
        tv_right.setOnClickListener(onClickListener);
    }

    /**
     * 隐藏右边TextView
     */
    protected void hideRightText() {
        tv_right.setVisibility(View.GONE);
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (mPresenter != null) {
            mPresenter.dettach();
        }
    }

}
