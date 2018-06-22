package com.freelink.library.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.freelink.library.R;


public abstract class BaseAlertDialog extends BaseNormalDialog {

    private TextView tvTitle;
    private FrameLayout flContent;
    @Override
    protected int getLayoutId() {
        return R.layout.free_dialog_alertbase;
    }

    protected abstract int getContentLayoutId();

    public BaseAlertDialog(@NonNull Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        tvTitle = rootView.findViewById(R.id.tv_dialog_title);
        flContent = rootView.findViewById(R.id.fl_dialog_content);

        if(getContentLayoutId() > 0) {
            LayoutInflater.from(context).inflate(getContentLayoutId(), flContent);
        }

    }

    public BaseAlertDialog title(String title) {
        if(title != null) {
            tvTitle.setText(title);
        }
        return this;
    }


}
