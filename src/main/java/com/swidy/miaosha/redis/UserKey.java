package com.swidy.miaosha.redis;

public class UserKey extends BasePrefix {

	public UserKey(String prefix) {
		super(prefix);
	}
	public static final UserKey getById = new UserKey("id");
	public static final UserKey getByName = new UserKey("name");

}
