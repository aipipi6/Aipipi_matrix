package com.freelink.library.widget;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.freelink.library.R;

import java.util.ArrayList;
import java.util.List;

public class WheelView extends ScrollView {
    public static final String TAG = WheelView.class.getSimpleName();

    public interface OnWheelViewListener {
        void onSelected(int selectedIndex, String item);
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private OnWheelViewListener onWheelViewListener;
    private Context context;
    private LinearLayout rootView;
    private int offset; // 偏移量（需要在最前面和最后面补全）
    private int selectedIndex = 1;
    private int initialY;
    private int newCheck = 50;
    private List<String> items;
    private int displayItemCount = 5;
    private int selectedTextColor = 0xFF333333;
    private int selectedTextSize = dp2px(16);
    private int nomalTextColor = 0xFF999999;
    private int nomalTextSize = dp2px(14);
    private int itemHeight = dp2px(40);
    private int backgroundColor = 0xFFFFFFFF;
    private int lineColor = 0xFFF7F7F7;

    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
            displayItemCount = a.getInt(R.styleable.WheelView_itemCount, displayItemCount);
            selectedTextColor = a.getColor(R.styleable.WheelView_selectedTextColor, selectedTextColor);
            selectedTextSize = a.getDimensionPixelSize(R.styleable.WheelView_selectedTextSize, selectedTextSize);
            nomalTextColor = a.getColor(R.styleable.WheelView_nomalTextColor, nomalTextColor);
            nomalTextSize = a.getDimensionPixelSize(R.styleable.WheelView_nomalTextSize, nomalTextSize);
            itemHeight = a.getDimensionPixelSize(R.styleable.WheelView_itemHeight, itemHeight);
            backgroundColor = a.getColor(R.styleable.WheelView_backgroundColor, backgroundColor);
            lineColor = a.getColor(R.styleable.WheelView_lineColor, lineColor);
            a.recycle();
        }
        offset = displayItemCount / 2;
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

        rootView = new LinearLayout(context);
        rootView.setOrientation(LinearLayout.VERTICAL);
        this.addView(rootView);

        selectedIndex = offset;

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scrollTo(0, (selectedIndex - offset) * itemHeight);
                getViewTreeObserver().removeOnPreDrawListener(this);
//                onSeletedCallBack();
                return true;
            }
        });
    }

    public void setItems(List<String> list) {
        setItems(list, 0);
    }

    public void setItems(List<String> list, int index) {
        if(list == null) {
            return;
        }
        if (items == null) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        if(index >= 0) {
            this.selectedIndex = index + offset;
        }
        initData();
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;
        rootView.removeAllViews();
        for (String item : items) {
            rootView.addView(createView(item));
        }
        fullScroll(ScrollView.FOCUS_UP); // 滑动到顶部
        refreshItemView(0);
    }

    Runnable scrollerTask = new Runnable() {

        @Override
        public void run() {

            int newY = getScrollY();
            if (initialY - newY == 0) { // stopped
                final int remainder = initialY % itemHeight;
                final int divided = initialY / itemHeight;
//                    Log.d(TAG, "initialY: " + initialY);
//                    Log.d(TAG, "remainder: " + remainder + ", divided: " + divided);
                if (remainder == 0) {
                    selectedIndex = divided + offset;

                    onSeletedCallBack();
                } else {
                    if (remainder > itemHeight / 2) {
                        WheelView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                selectedIndex = divided + offset + 1;
                                onSeletedCallBack();
                            }
                        });
                    } else {
                        WheelView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                WheelView.this.smoothScrollTo(0, initialY - remainder);
                                selectedIndex = divided + offset;
                                onSeletedCallBack();
                            }
                        });
                    }


                }
            } else {
                initialY = getScrollY();
                WheelView.this.postDelayed(scrollerTask, newCheck);
            }
        }
    };

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setSingleLine(true);
        tv.setText(item);
        tv.setMinHeight(itemHeight);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = rootView.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) rootView.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(selectedTextColor);
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTextSize);
            } else {
                itemView.setTextColor(nomalTextColor);
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_PX, nomalTextSize);
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    int viewWidth;

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            Log.d(TAG, "viewWidth: " + viewWidth);
        }

        background = new ColorDrawable(backgroundColor) {
            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);
                Paint paint = new Paint();
                paint.setColor(lineColor);
                paint.setStrokeWidth(dp2px(1f));

                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], paint);
//                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[0], paint);
//                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[1], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], paint);
            }
        };
        super.setBackgroundDrawable(background);
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
        if (t > oldt) {
//            Log.d(TAG, "向下滚动");
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
//            Log.d(TAG, "向上滚动");
            scrollDirection = SCROLL_DIRECTION_UP;

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    public String getSelectedText() {
        if(items != null && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        } else {
            return null;
        }
    }

    public int getSelectedIndex() {
        return selectedIndex - offset;
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex - offset, items.get(selectedIndex));
        }
    }

    public int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}