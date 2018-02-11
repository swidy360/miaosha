package com.swidy.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{

	private static final int EXPIRE_TOKEN = 24*60*60;
	
	public MiaoshaUserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static MiaoshaUserKey token = new MiaoshaUserKey(EXPIRE_TOKEN,"tk");
	public static MiaoshaUserKey getUserById = new MiaoshaUserKey(60,"id");
	

}
