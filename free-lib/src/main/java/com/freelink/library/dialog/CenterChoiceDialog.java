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

public class CenterChoiceDialog extends BaseNormalDialog {

    private LinearLayout llContent;
    private List<String> items;

    public CenterChoiceDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.free_dialog_centerchoice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px(120), dp2px(40));
            tvItem.setLayoutParams(layoutParams);
            tvItem.setText(item);
            tvItem.setBackgroundResource(R.drawable.bg_bottomdialog_button);
            tvItem.setClickable(true);
            tvItem.setTextColor(ContextCompat.getColor(context, R.color.blue));
            tvItem.setTextSize(16);
            tvItem.setGravity(Gravity.CENTER);
            llContent.addView(tvItem);

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
}
