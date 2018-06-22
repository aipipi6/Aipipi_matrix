package com.freelink.library.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freelink.library.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chenjun on 2018/6/11.
 */

public class BottomChoiceDialog extends BaseNormalDialog implements View.OnClickListener {

    private LinearLayout llContent;
    private List<String> items;
    private OnChoiceListener onChoiceListener;

    public BottomChoiceDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.free_dialog_bottomchoice;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWindowAnimations() {
        return  R.style.FromBottomAnimation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        llContent = findViewById(R.id.ll_dialog_content);
        refresh();
    }

    private void refresh() {
        if(llContent == null) {
            return;
        }

        llContent.removeAllViews();

        for(int i = 0; i < items.size(); i++) {

            String item = items.get(i);

            TextView tvItem = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(40));
            tvItem.setLayoutParams(layoutParams);
            tvItem.setText(item);
            tvItem.setBackgroundResource(R.drawable.bg_bottomdialog_button);
            tvItem.setClickable(true);
            tvItem.setTextColor(ContextCompat.getColor(context, R.color.blue));
            tvItem.setTextSize(16);
            tvItem.setGravity(Gravity.CENTER);
            llContent.addView(tvItem);
            tvItem.setTag(i);
            tvItem.setOnClickListener(this);

            if(i < items.size() - 1) {
                View spliteline = new View(context);
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                spliteline.setLayoutParams(layoutParams);
                spliteline.setBackgroundResource(R.color.spliteline);
                llContent.addView(spliteline);
            }
        }
    }


    public void setItems(List<String> items) {
        this.items = items;
        refresh();
    }

    public void setItems(String... items) {
        this.items = Arrays.asList(items);
        refresh();
    }

    public void setOnChoiceListener(OnChoiceListener onChoiceListener) {
        this.onChoiceListener = onChoiceListener;
    }

    @Override
    public void onClick(View v) {

        if(onChoiceListener != null) {
            TextView textView = (TextView) v;
            int index = (int) textView.getTag();
            String item = textView.getText().toString();
            onChoiceListener.onChoice(index, item);
            dismiss();
        }
    }

    public interface OnChoiceListener {
        void onChoice(int index, String item);
    }
}
