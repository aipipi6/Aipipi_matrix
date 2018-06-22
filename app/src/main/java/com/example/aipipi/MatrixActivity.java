package com.example.aipipi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.aipipi.custom.widget.DotMatrixView;
import com.example.aipipi.utils.StringUtil;
import com.example.aipipi.utils.font.FontUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MatrixActivity extends AppCompatActivity {


    @BindView(R.id.multiDotMatrixView)
    DotMatrixView dotMatrixView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        List<byte[]> fontList = FontUtils.makeFont24("楷体", "就喜欢皮一下");
        dotMatrixView.setMatrix(FontUtils.convertMatrix24(fontList));
        dotMatrixView.startScroll(100);
    }

}
