package com.freelink.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.freelink.library.viewHelper.CornerDrawableMaker;
import com.freelink.library.widget.loading.AVLoadingIndicatorView;


public class LoadingDialog extends Dialog {

    private int mSize = dp2px(120);
    private int mBackgroundColor = 0x60000000;
    private int mBackgroundRadius = dp2px(8);
    private int mProgressViewSize = dp2px(64);
    private int mPorgressViewColor = 0xFFFFFFFF;
    private int mSpace = dp2px(4);
    private String mText;
    private int mTextSize = dp2px(13);
    private int mTextColor = 0xFFFFFFFF;

    private Handler mHandler = new Handler();

    private TextView tvContent;
    private TextView mTvTime;
    private int mTimeTextColor = 0xFFFFFFFF;
    private int mTimeTextSize = dp2px(20);
    private int mTotalTime;

    public LoadingDialog(@NonNull Context context) {
        this(context, "正在加载", 0);
    }

    public LoadingDialog(@NonNull Context context,  String text) {
        this(context, text, 0);
    }

    public LoadingDialog(@NonNull Context context, String text, int totalTime) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// android:windowNoTitle
        getWindow().getAttributes().gravity = Gravity.CENTER;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);// android:windowBackground
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);// android:backgroundDimEnabled默认是true的
        getWindow().setDimAmount(0f);
        this.mText = text;
        this.mTotalTime = totalTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout rootView = new LinearLayout(getContext());
        setContentView(rootView, new ViewGroup.LayoutParams(mSize, mSize));
        rootView.setGravity(Gravity.CENTER);
        rootView.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setCornerRadius(mBackgroundRadius);
        bgDrawable.setColor(mBackgroundColor);
        rootView.setBackground(bgDrawable);

        FrameLayout flProgress = new FrameLayout(getContext());
        flProgress.setLayoutParams(new ViewGroup.LayoutParams(mProgressViewSize, mProgressViewSize));

        AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(getContext());
        progressView.setIndicatorColor(mPorgressViewColor);
        progressView.setIndicatorId(AVLoadingIndicatorView.BallSpinFadeLoader);
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        flProgress.addView(progressView);


        mTvTime = new TextView(getContext());
        mTvTime.setText(mTotalTime + "");
        mTvTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTimeTextSize);
        mTvTime.setTextColor(mTimeTextColor);
        flProgress.addView(mTvTime);

        if(mTotalTime > 0) {
            mTvTime.setVisibility( View.VISIBLE);
            mHandler.postDelayed(downTimeTask, 1000);
        } else {
            mTvTime.setVisibility( View.GONE);
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mTvTime.setLayoutParams(layoutParams);


        Space space = new Space(getContext());
        space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mSpace));

        tvContent = new TextView(getContext());
        tvContent.setText(mText);
        tvContent.setTextColor(mTextColor);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tvContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        rootView.addView(flProgress);
        rootView.addView(space);
        rootView.addView(tvContent);
    }

    Runnable downTimeTask = new Runnable() {
        @Override
        public void run() {
            mTotalTime--;
            if(mTotalTime <= 0) {
                dismiss();
            } else {
                mTvTime.setText(mTotalTime + "");
                mHandler.postDelayed(this, 1000);
            }

        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        mHandler.removeCallbacks(downTimeTask);
    }

    public LoadingDialog size(float dp) {
        mSize = dp2px(dp);
        return this;
    }

    public LoadingDialog backgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
        return this;
    }

    public LoadingDialog backgroundRadius(float radiusDP) {
        mBackgroundRadius = dp2px(radiusDP);
        return this;
    }

    public LoadingDialog progressViewSize(float dp) {
        mProgressViewSize = dp2px(dp);
        return this;
    }

    public LoadingDialog progressViewColor(@ColorInt int color) {
        mPorgressViewColor = color;
        return this;
    }

    public LoadingDialog space(float spaceDP) {
        mSpace = dp2px(spaceDP);
        return this;
    }

    public LoadingDialog text(String text) {
        mText = text;
        return this;
    }

    public LoadingDialog textSize(float sizeSp) {
        mTextSize = dp2px(sizeSp);
        return this;
    }

    public LoadingDialog textColor(@ColorInt int color) {
        mTextColor = color;
        return this;
    }

    public void setContentText(String text) {
        tvContent.setText(text);
    }


    private int dp2px(float dpValue) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dpValue);
    }
}
