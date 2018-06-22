package com.example.aipipi;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.example.aipipi.base.BaseToolBarActivity;
import com.freelink.library.viewHelper.RadioGroupHelper;

import butterknife.BindView;

public class MainActivity extends BaseToolBarActivity {

    @BindView(R.id.ll_font)
    LinearLayout llFont;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        showToolBar(false);

        final RadioGroupHelper radioGroupHelper = new RadioGroupHelper(llFont);

        radioGroupHelper.setOnCheckedChangeListener(new RadioGroupHelper.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View v, int index) {
                LogUtils.e(index + "/" + radioGroupHelper.getCheckedRadioIndex());
            }
        });
    }

}
