package com.example.aipipi.utils.font;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class Font16 {

	private final static String ENCODE = "GB2312";
	private static Map<String, String> fontStyleMap = new HashMap<>();
	static {
		fontStyleMap.put("仿宋", "f");
		fontStyleMap.put("黑体", "h");
		fontStyleMap.put("楷体", "k");
		fontStyleMap.put("宋体", "s");
		fontStyleMap.put("幼圆", "y");
	}
	
	static List<byte[]> makeFont(String fontStyle, String hzString, OnMakeFontListener onMakeFontListener) {
		
		List<byte[]> fontHexList = new ArrayList<>();
		 
		try {
			for (int i = 0; i < hzString.length(); i++) {
				String hz = hzString.substring(i, i + 1);
				long IOffset = 0;
				int fontHexLen = 0;
				
				String zkFileName = null;
				
				char hzChar = hz.charAt(0);
				if(FontUtils.isAscII(hzChar)) {
					zkFileName = "font/asc/ASC16_8";
					IOffset = (hzChar - 32) * 16;
					fontHexLen = 16;
				} else if(FontUtils.isChinese(hzChar)){
					String c = fontStyleMap.get(fontStyle);
					zkFileName = "font/16x16/hzk16" + (c == null ? "s" : c);
					IOffset = getOffset(hz);
					fontHexLen = 32;
				} else {
					continue;
				}

				InputStream is = Utils.getApp().getAssets().open(zkFileName);
				is.skip(IOffset);
				byte[] fontHex = new byte[fontHexLen];
				is.read(fontHex);
				is.close();
				
				if(hzChar > 0x80) {
					boolean[][] points = new boolean[16][16];
					for (int x = 0; x < 16; x++) {
						for (int y = 0; y < 2; y++) {
							for (int z = 0; z < 8; z++) {
								if ((fontHex[x * 2 + y] & (0x80 >> z)) >= 1) {
									points[y * 8 + z][x] = true;
								} else {
									points[y * 8 + z][x] = false;
								}
							}
						}
					}
					
					Arrays.fill(fontHex, (byte) 0);
					for(int x = 0; x < points.length; x++) {
						for(int y = 0; y < points[x].length; y++) {
							int index = x * points[x].length + y;
							index = index / 8;
							if(points[x][y]) {
								fontHex[index] |= 1 << (7 - y % 8);
							}
						}
					}
				}
				fontHexList.add(fontHex);

				if(onMakeFontListener != null) {
					onMakeFontListener.schedule(i, hzString.length());
				}

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

			int area = areaCode - 0xA0;
			int pos = posCode - 0xA0;

			ioffset = 32 * ((area - 1) * 94 + pos - 1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return ioffset;
	}
}
