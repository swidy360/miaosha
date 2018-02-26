package com.swidy.miaosha.redis;


public class MiaoshaKey extends BasePrefix {

	public MiaoshaKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static final MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp"); 
	public static final MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");

}
