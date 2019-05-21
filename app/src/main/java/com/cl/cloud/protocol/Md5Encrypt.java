/**
 * @description 用于Md5加密,得到固定长度的数据
 * @version 1.0
 * @author figo
 */

package com.cl.cloud.protocol;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Encrypt {

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuilder hexValue = new StringBuilder();
		for (byte md5Byte : md5Bytes) {
			int val = ((int) md5Byte);
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static String MD5_2(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(string.getBytes());
			String result = "";
			for (byte b : bytes) {
				String temp = Integer.toHexString(b & 0xff);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				result += temp;
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String get32BitMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };// 用来将字节转换成16进制表示的字符
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
			// 进制需要 32 个字符
			int k = 0;// 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
				// 进制字符的转换
				byte byte0 = tmp[i];// 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
				// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

			}
			s = new String(str);// 换后的结果转换为字符串
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String get16BitMD5(byte[] source){
		String str = get32BitMD5(source);
		return str.substring(8, 24);
	}

	public static byte[] get16BitMD5Byte(byte[] source) {
		byte tmp[] = null;
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			md.update(source);
			tmp = md.digest(); // MD5 的计算结果是一个 128 位的长整数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp;
	}

}
