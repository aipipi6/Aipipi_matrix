package com.freelink.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.freelink.library.R;


public class ImageTextView extends AppCompatTextView {
    public static final int LEFT = 1, TOP = 2, RIGHT = 3, BOTTOM = 4;

    private int mDrawableSize, mLeftSize, mRightSize, mTopSize, mBottomSize;
    private Drawable mDrawableLeft = null, mDrawableTop = null, mDrawableRight = null, mDrawableBottom = null;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ImageTextView);
        mDrawableSize = a.getDimensionPixelSize(R.styleable.ImageTextView_drawableSize, -1);
        mLeftSize = a.getDimensionPixelSize(R.styleable.ImageTextView_drawableLeftSize, dp2px(30));
        mRightSize= a.getDimensionPixelSize(R.styleable.ImageTextView_drawableRightSize, dp2px(30));
        mTopSize = a.getDimensionPixelSize(R.styleable.ImageTextView_drawableTopSize, dp2px(30));
        mBottomSize = a.getDimensionPixelSize(R.styleable.ImageTextView_drawableBottomSize, dp2px(30));
        mDrawableTop = a.getDrawable(R.styleable.ImageTextView_drawableTop);
        mDrawableBottom = a.getDrawable(R.styleable.ImageTextView_drawableBottom);
        mDrawableRight = a.getDrawable(R.styleable.ImageTextView_drawableRight);
        mDrawableLeft = a.getDrawable(R.styleable.ImageTextView_drawableLeft);

        a.recycle();

        if(mDrawableSize > 0) {
            mLeftSize = mDrawableSize;
            mRightSize = mDrawableSize;
            mTopSize = mDrawableSize;
            mBottomSize = mDrawableSize;
        }

        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
                                                        Drawable top, Drawable right, Drawable bottom) {

        if (left != null) {
            left.setBounds(0, 0, mLeftSize, mLeftSize);
        }
        if (right != null) {
            right.setBounds(0, 0, mRightSize, mRightSize);
        }
        if (top != null) {
            top.setBounds(0, 0, mTopSize, mTopSize);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, mBottomSize, mBottomSize);
        }
        setCompoundDrawables(left, top, right, bottom);
    }

    public void setLeftDrawable(@DrawableRes int resId) {
        mDrawableLeft = ContextCompat.getDrawable(getContext(), resId);
        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom);
    }

    public void setRightDrawable(@DrawableRes int resId) {
        mDrawableRight = ContextCompat.getDrawable(getContext(), resId);
        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom);
    }

    public void setTopDrawable(@DrawableRes int resId) {
        mDrawableTop = ContextCompat.getDrawable(getContext(), resId);
        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom);
    }

    public void setBottomDrawable(@DrawableRes int resId) {
        mDrawableBottom = ContextCompat.getDrawable(getContext(), resId);
        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom);
    }


    public int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
