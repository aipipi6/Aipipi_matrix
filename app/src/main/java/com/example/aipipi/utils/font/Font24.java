package com.example.aipipi.utils.font;

import com.blankj.utilcode.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Font24 {

	private final static String ENCODE = "GB2312";
	private static Map<String, String> fontStyleMap = new HashMap<>();
	static {
		fontStyleMap.put("仿宋", "F");
		fontStyleMap.put("黑体", "H");
		fontStyleMap.put("楷体", "K");
		fontStyleMap.put("宋体", "S");
	}

	static List<byte[]> makeFont(String fontStyle, String hzString) {

		List<byte[]> fontHexList = new ArrayList<>();


		try {
			for (int i = 0; i < hzString.length(); i++) {
				String hz = hzString.substring(i, i + 1);
				long IOffset = 0;
				int fontHexLen = 0;
				
				String zkFileName = null;
				
				char hzChar = hz.charAt(0);
				if(FontUtils.isAscII(hzChar)) {
					zkFileName = "font/asc/ASC24_12";
					IOffset = (hzChar - 32) * 36;
					fontHexLen = 36;
				} else if(FontUtils.isChinesePunctuation(hzChar)) {
					zkFileName = "font/24x24/HZK24T";
					IOffset = getPunctuationOffset(hz);
					fontHexLen = 72;
				} else {
					String c = fontStyleMap.get(fontStyle);
					zkFileName =  "font/24x24/HZK24" + (c == null ? "S" : c);
					IOffset = getOffset(hz);
					fontHexLen = 72;
				}
				InputStream is = Utils.getApp().getAssets().open(zkFileName); //new FileInputStream(zkFilePath);
				is.skip(IOffset);
				byte[] fontHex = new byte[fontHexLen];
				is.read(fontHex);
				is.close();
				
				fontHexList.add(fontHex);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fontHexList;
	}

	private static long getOffset(String hz) {
		long ioffset = 0;
		try {
			byte[] gb2312Code = hz.getBytes(ENCODE);
			int areaCode = gb2312Code[0] < 0 ? 256 + gb2312Code[0] : gb2312Code[0];
			int posCode = gb2312Code[1] < 0 ? 256 + gb2312Code[1] : gb2312Code[1];

			int area = areaCode - 0xAF;
			int pos = posCode - 0xA0;
			
			ioffset = 72 * ((area - 1) * 94 + pos - 1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return ioffset;
	}
	
	private static long getPunctuationOffset(String hz) {
		
		long ioffset = 0;
		try {
			byte[] gb2312Code = hz.getBytes(ENCODE);
			int areaCode = gb2312Code[0] < 0 ? 256 + gb2312Code[0] : gb2312Code[0];
			int posCode = gb2312Code[1] < 0 ? 256 + gb2312Code[1] : gb2312Code[1];

			int area = areaCode - 0xA0;
			int pos = posCode - 0xA0;

			ioffset = 72 * ((area - 1) * 94 + pos - 1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return ioffset;
	}

}
