package com.swidy.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix {
	
	private int expireSeconds;
	
	private String prefix;
	
	public BasePrefix(String prefix) {
		this(0,prefix);//0-代表永不过期
	}

	public BasePrefix(int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	@Override
	public int expireSeconds() {
		return expireSeconds;
	}

	@Override
	public String getPrefix() {
		String className = this.getClass().getSimpleName();
		return className + ":" + this.prefix;
	}

}
