package com.freelink.library.viewHelper;


import android.view.View;
import android.view.ViewGroup;

public class RadioGroupHelper implements View.OnClickListener {

    private ViewGroup viewGroup;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int checkedRadioIndex;

    public RadioGroupHelper(ViewGroup viewGroup, int checkedRadioIndex) {
        this.viewGroup = viewGroup;
        this.checkedRadioIndex = checkedRadioIndex;

        int childCount = viewGroup.getChildCount();
        for(int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            childView.setOnClickListener(this);
            childView.setTag(i);

            childView.setSelected(i == checkedRadioIndex);
        }
    }

    @Override
    public void onClick(View v) {
        int childCount = viewGroup.getChildCount();
        int currentIndex = (int) v.getTag();
        if(checkedRadioIndex == currentIndex) {
            return;
        }
        checkedRadioIndex = currentIndex;
        for(int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            int index = (int) childView.getTag();
            childView.setSelected(index == checkedRadioIndex);
        }

        if(onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(v, checkedRadioIndex);
        }
    }

    public int getCheckedRadioIndex() {
        return checkedRadioIndex;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View v, int index);
    }
}
