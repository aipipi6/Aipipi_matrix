package com.freelink.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freelink.library.R;


public class CustomAlertDialog extends BaseAlertDialog {

    private TextView tvContent;
    private LinearLayout llContent;
    private TextView tvLeft;
    private TextView tvRight;

    private OnClickListener onClickListener;

    @Override
    protected int getContentLayoutId() {
        return R.layout.free_dialog_alertcustom;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    public CustomAlertDialog(Context context) {
        super(context);

        tvContent = findViewById(R.id.tv_dialog_content);
        llContent = findViewById(R.id.ll_dialog_content);
        tvLeft = findViewById(R.id.tv_dialog_left);
        tvRight = findViewById(R.id.tv_dialog_right);

        tvLeft.setOnClickListener(btnClickListener);
        tvRight.setOnClickListener(btnClickListener);
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onClickListener == null) {
                dismiss();
                return;
            }
            if(view.getId() == R.id.tv_dialog_left) {
                onClickListener.onLeftClick(CustomAlertDialog.this);
            } else if(view.getId() == R.id.tv_dialog_right) {
                onClickListener.onRightClick(CustomAlertDialog.this);
            }
        }
    };


    public CustomAlertDialog content(String content) {
        if(content != null) {
            llContent.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(content);
        }
        return this;
    }

    public CustomAlertDialog content(View content) {
        if(content != null) {
            llContent.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.GONE);
            llContent.addView(content);
        }
        return this;
    }

    public CustomAlertDialog left(String left) {
        if(left != null) {
            tvLeft.setText(left);
        }
        return this;
    }

    public CustomAlertDialog right(String right) {
        if(right != null) {
            tvRight.setText(right);
        }
        return this;
    }

    public CustomAlertDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        void onLeftClick(CustomAlertDialog dialog);
        void onRightClick(CustomAlertDialog dialog);
    }

    public static Dialog show(Context context, String title, String content, String left, String right, OnClickListener onClickListener) {
        CustomAlertDialog dialog = new CustomAlertDialog(context);
        dialog.content(content)
                .left(left).right(right)
                .setOnClickListener(onClickListener)
                .show();

        return dialog;
    }
    public static Dialog show(Context context, String title, View content, String left, String right, OnClickListener onClickListener) {
        CustomAlertDialog dialog = new CustomAlertDialog(context);
        dialog.title(title);
        dialog.content(content)
                .left(left).right(right)
                .setOnClickListener(onClickListener)
                .show();

        return dialog;
    }

    public static Dialog show(Context context, String content, String left, String right, OnClickListener onClickListener) {
        return show(context, "提示", content, left, right, onClickListener);
    }

    public static Dialog show(Context context, View content, String left, String right, OnClickListener onClickListener) {
        return show(context, "提示", content, left, right, onClickListener);
    }

}
