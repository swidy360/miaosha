package com.swidy.miaosha.redis;

public class GoodsKey extends BasePrefix{

	public GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static final GoodsKey getGoodList = new GoodsKey(60,"gl");
	public static final GoodsKey getGoodDetail = new GoodsKey(60,"gd");

}
