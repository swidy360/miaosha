package com.swidy.miaosha.redis;

public class AccessKey extends BasePrefix {

	private AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static AccessKey withExpire(int expiresSeconds){
		return new AccessKey(expiresSeconds,"access");
	}
	
	
	public static final AccessKey ak = new AccessKey(500,"test");
	

}
