package com.swidy.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatorUtil {
	
	private static final Pattern mobile_partern = Pattern.compile("1\\d{10}");
	
	public static boolean isMobile(String str){
		if(StringUtils.isEmpty(str)){
			return false;
		}
		Matcher matcher = mobile_partern.matcher(str);
		return matcher.matches();
	}
	
	public static void main(String[] args) {
		System.out.println(isMobile("13012344321"));
		System.out.println(isMobile("1301234432"));
	}
}
