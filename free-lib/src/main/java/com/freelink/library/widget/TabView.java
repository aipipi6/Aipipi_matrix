package com.freelink.library.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freelink.library.R;


public class TabView extends RelativeLayout {

    private ImageView iv_icon;
    private TextView tv_name;
    private TextView tv_count;
    private ImageView iv_indicate;

    private int mCount;

    public TabView(Context context) {
        super(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabView);
        Boolean isSelected = a.getBoolean(R.styleable.TabView_tabSelected, false);
        setSelected(isSelected);
        Drawable drawable = a.getDrawable(R.styleable.TabView_tabDrawable);
        int drawableSize = a.getDimensionPixelSize(R.styleable.TabView_tabDrawableSize, dp2px(32));
        String text = a.getString(R.styleable.TabView_tabText);
        int textSize = a.getDimensionPixelSize(R.styleable.TabView_tabTextSize, dp2px(15));
        ColorStateList textColor = a.getColorStateList(R.styleable.TabView_tabTextColor);
        int drawablePadding = a.getDimensionPixelSize(R.styleable.TabView_tabDrawablePadding, dp2px(2));

        int countTextSize = a.getDimensionPixelSize(R.styleable.TabView_countTextSize, dp2px(10));
        int countTextColor = a.getColor(R.styleable.TabView_countTextColor,
                ContextCompat.getColor(context, android.R.color.white));
        String countText = a.getString(R.styleable.TabView_countText);
        int countTextBackgroundColor = a.getColor(R.styleable.TabView_countBackgroundColor,
                ContextCompat.getColor(context, android.R.color.holo_red_light));
        int indicateColor = a.getColor(R.styleable.TabView_indicateColor,
                ContextCompat.getColor(context, android.R.color.holo_red_light));
        int indicateSize = a.getDimensionPixelSize(R.styleable.TabView_indicateSize, dp2px(8));

        ratio = a.getFloat(R.styleable.TabView_ratio, ratio);
        sizeMode = a.getInt(R.styleable.TabView_sizeMode, sizeMode);

        a.recycle();

        LinearLayout rootView = new LinearLayout(context);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setId(View.generateViewId());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(rootView, lp);

        iv_icon = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = drawableSize;
        params.height = drawableSize;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        iv_icon.setLayoutParams(params);
        iv_icon.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_icon.setImageDrawable(drawable);
        iv_icon.setSelected(isSelected());
        rootView.addView(iv_icon);

        tv_name = new TextView(context);
        params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = drawablePadding;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        tv_name.setLayoutParams(params);
        if (textColor != null) {
            tv_name.setTextColor(textColor);
        }
        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_name.setText(text);
        tv_name.setSelected(isSelected());
        rootView.addView(tv_name);

        tv_count = new TextView(context);
        tv_count.setTextColor(countTextColor);
        tv_count.setGravity(Gravity.CENTER);
        tv_count.setMinWidth(dp2px(14));
        tv_count.setMinHeight(dp2px(14));
        tv_count.setPadding(dp2px(4), 0, dp2px(4), 0);
        tv_count.setBackgroundDrawable(getCornerDrawable(countTextBackgroundColor, countTextSize));
        tv_count.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize);
        tv_count.setText(countText);
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_TOP, rootView.getId());
        lp.addRule(RelativeLayout.ALIGN_LEFT, rootView.getId());
        lp.leftMargin = drawableSize - dp2px(7);
        addView(tv_count, lp);
        tv_count.setVisibility(View.GONE);

        iv_indicate = new ImageView(context);
        iv_indicate.setImageDrawable(getPointDrawable(indicateColor, indicateSize));
        lp = new LayoutParams(indicateSize, indicateSize);
        lp.addRule(RelativeLayout.ALIGN_TOP, rootView.getId());
        lp.addRule(RelativeLayout.ALIGN_LEFT, rootView.getId());
        lp.leftMargin = drawableSize -indicateSize / 2;
        addView(iv_indicate, lp);
        iv_indicate.setVisibility(View.GONE);
    }

    private float ratio = 1f; // 宽高比例
    private int sizeMode = 0;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(sizeMode == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int w = 0, h = 0;
            if (sizeMode == 1) {
                w = MeasureSpec.getSize(widthMeasureSpec);
                h = (int) (w / ratio);
            } else {
                h = MeasureSpec.getSize(heightMeasureSpec);
                w = (int) (h * ratio);
            }
            setMeasuredDimension(w, h);
        }
    }

    public void setCountText(int count) {
        iv_indicate.setVisibility(View.GONE);
        if(count > 0) {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText(count < 100 ? String.valueOf(count) : "99+");
        } else {
            tv_count.setVisibility(View.GONE);
        }
        mCount = count;
    }

    public int getCount() {
        return mCount;
    }

    public void showIndicate(boolean isShow) {
        iv_indicate.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public boolean isShowIndicate() {
        return iv_indicate.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (iv_icon != null) {
            iv_icon.setSelected(selected);
        }
        if (tv_name != null) {
            tv_name.setSelected(selected);
        }
    }

    public String getNameText() {
        return tv_name.getText().toString();
    }

    public int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public Drawable getCornerDrawable(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(color);
        return drawable;
    }

    public Drawable getPointDrawable(int color, int radius) {
        float[] radiuses = new float[8];
        for(int i = 0; i < radiuses.length; i++) {
            radiuses[i] = radius;
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(radiuses);
        drawable.setColor(color);
        return drawable;
    }

}

