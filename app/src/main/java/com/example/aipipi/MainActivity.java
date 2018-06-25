package com.example.aipipi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.example.aipipi.base.BaseToolBarActivity;
import com.example.aipipi.custom.widget.DotMatrixView;
import com.example.aipipi.utils.StringUtil;
import com.example.aipipi.utils.font.FontUtils;
import com.freelink.library.viewHelper.RadioGroupHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseToolBarActivity {

    @BindView(R.id.ll_font)
    LinearLayout llFont;

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.multiDotMatrixView)
    DotMatrixView dotMatrixView;


    final static String[] sFontTyps = {"宋体", "黑体", "仿宋", "楷体"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
    RadioGroupHelper radioGroupHelper;
    @Override
    protected void initData(Bundle savedInstanceState) {
        showToolBar(false);

        radioGroupHelper = new RadioGroupHelper(llFont, 0);

        radioGroupHelper.setOnCheckedChangeListener(new RadioGroupHelper.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View v, int index) {
                LogUtils.e(index + "/" + radioGroupHelper.getCheckedRadioIndex());
            }
        });

    }


    @OnClick(R.id.tv_preview)
    void onPreview() {
        String text = editText.getText().toString();
        if(StringUtil.isEmpty(text)) {
            showToast("请输入文本");
            return;
        }
        int index = radioGroupHelper.getCheckedRadioIndex();

        List<byte[]> fontList = FontUtils.makeFont24(sFontTyps[index], text);
        dotMatrixView.setMatrix(FontUtils.convertMatrix24(fontList));
        dotMatrixView.startScroll(100);
    }
}
