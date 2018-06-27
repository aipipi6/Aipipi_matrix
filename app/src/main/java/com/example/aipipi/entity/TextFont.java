package com.example.aipipi.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjun on 2018/6/27.
 */

public class TextFont {

    private int fontSize;

    private String fontType;

    private String text;

    private List<byte[]> fontList;

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontType() {
        return fontType == null ? "" : fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<byte[]> getFontList() {
        if (fontList == null) {
            return new ArrayList<>();
        }
        return fontList;
    }

    public void setFontList(List<byte[]> fontList) {
        this.fontList = fontList;
    }
}
