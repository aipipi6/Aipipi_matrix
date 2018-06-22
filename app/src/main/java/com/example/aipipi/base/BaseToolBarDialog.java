package com.example.aipipi.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.aipipi.R;
import com.freelink.library.dialog.LoadingDialog;
import com.freelink.library.widget.ImageTextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseToolBarDialog<P extends BasePresenter> extends DialogFragment {

    private Unbinder unbinder;

    private ImageTextView ivBack;
    private TextView tvTitle;
    private TextView tvLeft;
    private TextView tvRight;
    private FrameLayout flContent;
    protected View contentView;
    protected Context context;
    private LoadingDialog loadingDialog;
    protected P mPresenter;
    protected Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getStyleRes());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.layout_base_toolbar, null);
        ivBack = (ImageTextView) rootView.findViewById(R.id.itv_base_back);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_base_title);
        tvLeft = (TextView) rootView.findViewById(R.id.tv_base_left);
        tvRight = (TextView) rootView.findViewById(R.id.tv_base_right);
        flContent = (FrameLayout) rootView.findViewById(R.id.fl_base_content);

        int layoutId = getLayoutId();
        if(layoutId > 0) {
            contentView = LayoutInflater.from(context).inflate(layoutId, null);
            unbinder = ButterKnife.bind(this, contentView);
            flContent.addView(contentView);
        }
        mPresenter = createPresenter();
        if(mPresenter != null) {
            mPresenter.attach(this);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(savedInstanceState);

    }

    protected abstract int getLayoutId();
    protected abstract void initData(Bundle savedInstanceState);
    protected P createPresenter() {
        return null;
    }

    protected void setShowFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fl_base_content, fragment).commit();
    }

    protected void setLeftText(String leftText, final View.OnClickListener onClickListener) {
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setText(leftText);
        tvLeft.setOnClickListener(onClickListener);
    }

    protected void setLeftText(String leftText) {
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setText(leftText);
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    protected void setRightText(String rightText) {
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(rightText);
    }

    protected void setTitleText(String title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    protected void showBackIcon(boolean isShow) {
        ivBack.setVisibility(isShow ? View.VISIBLE : View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    protected int getDimensionPixelSize(@DimenRes int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }

    protected int getStyleRes() {
        return R.style.FromBottomAnimation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder != null && unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
        }
    }

    /**
     * 显示加载进度dialog
     * @param content
     */
    public void showLoadingDialog(String content) {
        if(loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setTitle(content);
        } else {
            hideLoadingDialog();
            loadingDialog = new LoadingDialog(getActivity(), content);
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


    @Override
    public void dismiss() {
        super.dismiss();
        if(onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
