package com.swidy.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	
	public static String md5(String str){
		return DigestUtils.md5Hex(str);
	}
	
	private static final String salt = "1a2b3c4d";
	
	public static String inputPassToFormPass(String inputPass){
		String str = "" +  salt.charAt(1) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4); 
		return md5(str);
	}
	
	public static String formPassToDBPass(String formPass, String salt){
		String str = "" + salt.charAt(1) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
		return md5(str);
	}
	
	public static String inputPassToDBPass(String inputPass, String salt){
		String formPass = inputPassToFormPass(inputPass);
		String dbPass = formPassToDBPass(formPass, salt);
		return dbPass;
	}

	public static void main(String[] args) {
		System.out.println(inputPassToFormPass("123456"));//7f2a09611b18ae53dd02baa525d1e967
		System.out.println(formPassToDBPass(inputPassToFormPass("123456"), salt));//f79ab6650e6e0bac2a47a010607631bf
		System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));//f79ab6650e6e0bac2a47a010607631bf
		
		System.out.println('a' + 'b' + "10");
		System.out.println("" + 'a' + 'b' + "10");
		System.out.println('a' + 'b' + "10");
		System.out.println('a' + 'b' + "10");
		System.out.println('a' + "10" + 'b');
		System.out.println("10" + 'a' + 'b');
	}
}
