package com.example.aipipi.utils.font;

import java.util.List;

public class FontUtils {

	public static final int FONT_SIZE_16 = 16;
	public static final int FONT_SIZE_24 = 24;
	
	public static List<byte[]> makeFont16(String fontStyle, String hzString) {
		return makeFont(FONT_SIZE_16, fontStyle, hzString);
	}
	
	public static List<byte[]> makeFont24(String fontStyle, String hzString) {
		return makeFont(FONT_SIZE_24, fontStyle, hzString);
	}
	
	public static List<byte[]> makeFont(int fontSize, String fontStyle, String hzString) {
		
		List<byte[]>  fList = null;
		switch (fontSize) {
		
		case FONT_SIZE_16:
			fList = Font16.makeFont(fontStyle, hzString);
			break;
			
		case FONT_SIZE_24:
			fList = Font24.makeFont(fontStyle, hzString);
			break;

		default:
			break;
		}
		return fList;
	}

	public static boolean[][] convertMatrix16(List<byte[]> fontHexList) {
		return convertMatrix(FONT_SIZE_16, fontHexList);
	}
	public static boolean[][] convertMatrix24(List<byte[]> fontHexList) {
		return convertMatrix(FONT_SIZE_24, fontHexList);
	}
	public static boolean[][] convertMatrix(int fontSize, List<byte[]> fontHexList) {
		int x = 0, y = fontSize;
		for(byte[] fontHex : fontHexList) {
			x += fontHex.length * 8 / fontSize;
		}
		boolean[][] matrix = new boolean[x][y];

		int startX = 0;
		for(byte[] fontHex : fontHexList) {
			int column = fontSize;
			int row = fontSize / 8;
			if(fontHex.length == 16) {
				column = 8;
				row = 2;
			} else if(fontHex.length == 36) {
				column = 12;
				row = 3;
			}

			int i, j, k;
			for (i = 0; i < column; i++) {
				for (j = 0; j < row; j++) {
					for (k = 0; k < 8; k++) {
						if ((fontHex[i * row + j] & (0x80 >> k)) >= 1) {
							matrix[i + startX][j * 8 + k] = true;
						} else {
							matrix[i + startX][j * 8 + k] = false;
						}
					}
				}
			}
			startX += column;
		}
		return matrix;
	}

	public static boolean[][] makeMatrix16(String fontStyle, String hzString) {
		return makeMatrix(FONT_SIZE_16, fontStyle, hzString);
	}

	public static boolean[][] makeMatrix24(String fontStyle, String hzString) {
		return makeMatrix(FONT_SIZE_24, fontStyle, hzString);
	}

	public static boolean[][] makeMatrix(int fontSize, String fontStyle, String hzString) {
		List<byte[]> fontHexList =  makeFont(fontSize, fontStyle, hzString);
		return convertMatrix(fontSize, fontHexList);
	}

	
	public static void  printFontList(boolean isOriginal, List<byte[]> fontList) {
		for(byte[] font : fontList) {
			printFont(isOriginal, font);
		}
	}
	
	public static void printFont(boolean isOriginal, byte[] fontHex) {
		int fontSize = (int) Math.sqrt(fontHex.length * 8);
		int row = fontSize / 8;
		boolean[][] points; 
		if(fontHex.length == 16) {
			fontSize = 8;
			row = 2;
			points = new boolean[16][8];
		} else if(fontHex.length == 36) {
			fontSize = 12;
			row = 3;
			points = new boolean[24][12];
		} else {
			points = new boolean[fontSize][fontSize];
		}
		
		int i, j, k;
		for (i = 0; i < fontSize; i++) {
			for (j = 0; j < row; j++) {
				for (k = 0; k < 8; k++) {
					if ((fontHex[i * row + j] & (0x80 >> k)) >= 1) {
						points[j * 8 + k][i] = true;
						if(isOriginal) {
							System.out.print("①");
						}
					} else {
						points[j * 8 + k][i] = false;
						if(isOriginal) {
							System.out.print(" ");
						}
					}
				}
			}
			if(isOriginal) {
				System.out.println();
			}
		}
		
		if(!isOriginal) {
			for(i = 0; i < points.length; i++) {
				for(j = 0; j < points[i].length; j++) {
					if(points[i][j]) {
						System.out.print("��");
					} else {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
		}
	}
	
	static boolean isAscII(char c) {
		return c < 0x80;
	}


	static boolean isChinesePunctuation(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION 
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
				|| ub == Character.UnicodeBlock.VERTICAL_FORMS) {
			return true;
		} else {
			return false;
		}
	}

}
