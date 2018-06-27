package com.example.aipipi.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.aipipi.R;
import com.example.aipipi.base.BaseToolBarDialog;
import com.example.aipipi.entity.TextFont;
import com.example.aipipi.utils.font.FontUtils;
import com.example.aipipi.utils.font.OnMakeFontListener;
import com.freelink.library.dialog.BaseNormalDialog;
import com.freelink.library.widget.CircularProgressView;

import java.util.List;

/**
 * Created by chenjun on 2018/6/27.
 */

public class MakeFontDialog extends BaseNormalDialog {


    private static final String TAG = MakeFontDialog.class.getSimpleName();

    private TextFont textFont;
    private CircularProgressView circularProgressView;

    public MakeFontDialog(@NonNull Context context, TextFont textFont) {
        super(context);
        this.textFont = textFont;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_make_font;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        circularProgressView = findViewById(R.id.CircularProgressView);

        makeFontAsync();
    }

    private void makeFontAsync() {
        new AsyncTask<Void, Integer, List<byte[]>>() {
            @Override
            protected List<byte[]> doInBackground(Void... params) {

                return  FontUtils.makeFont(textFont.getFontSize(), textFont.getFontType(), textFont.getText(), new OnMakeFontListener() {
                    @Override
                    public void schedule(int current, int total) {
                        publishProgress(current, total);
                    }
                });
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int current = values[0];
                int total = values[1];
                float progress = current / (float) total;
                circularProgressView.setProgress(progress);
            }

            @Override
            protected void onPostExecute(List<byte[]> bytes) {
                dismiss();
                textFont.setFontList(bytes);
            }
        }.execute();
    }
}
