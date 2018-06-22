package com.freelink.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.freelink.library.R;


public class RatioImageView extends AppCompatImageView {

    private static final int BASE_CUSTOM = 0;
    private static final int BASE_WIDTH = 1;
    private static final int BASE_HEIGHT = 2;

    private float ratio = 1.0f;
    private int baseMode = BASE_CUSTOM;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
            ratio  = a.getFloat(R.styleable.RatioImageView_ratio, ratio);
            baseMode = a.getInt(R.styleable.RatioImageView_baseMode, baseMode);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(baseMode == BASE_CUSTOM) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int w = 0, h = 0;
        if(baseMode == BASE_WIDTH) {
            w = MeasureSpec.getSize(widthMeasureSpec);
            h = (int) (w / ratio);
        } else if(baseMode == BASE_HEIGHT){
            h = MeasureSpec.getSize(heightMeasureSpec);
            w = (int) (h * ratio);
        }
        setMeasuredDimension(w, h);
    }
}
