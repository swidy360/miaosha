package com.swidy.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;

@Service
public class RedisService {
	
	@Autowired
	JedisPool jedisPool;
	
	/**
	 * 获取单个对象
	 */
	public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String realKey = prefix.getPrefix() + key;
			String str = jedis.get(realKey);
			T t = stringToBean(str,clazz);
			return t;
		}finally {
			returnToPool(jedis);
		}
	}
	
	/**
	 * 设置对象
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean set(KeyPrefix prefix, String key, T value){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String str = beanToString(value);
			if(str == null || str.length() <= 0){
				return false;
			}
			String realKey = prefix.getPrefix() + key;
			int seconds = prefix.expireSeconds();
			if(seconds > 0){
				jedis.setex(realKey, seconds, str);
			}else{
				jedis.set(realKey, str);
			} 
			return true;
		}finally{
			returnToPool(jedis);
		} 
	}
	
	private void returnToPool(Jedis jedis) {
		if(jedis != null){
			jedis.close();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T stringToBean(String str, Class<T> clazz){
		if(str == null || str.length() <= 0 || clazz == null){
			return null;
		}
		if(clazz == String.class){
			return (T) str;
		}else if(clazz == int.class || clazz == Integer.class){
			return (T) str;
		}else if(clazz == long.class || clazz == Long.class){
			return (T) str;
		}else{
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}
	
	private <T> String beanToString(T value){
		if(value == null){
			return null;
		}
		Class<? extends Object> clazz = value.getClass();
		if(clazz == String.class){
			return (String) value;
		}else if(clazz == int.class || clazz == Integer.class){
			return "" + value;
		}else if(clazz == long.class || clazz == Long.class){
			return "" + value;
		}else{
			return JSON.toJSONString(value);
		} 
	}
	

}
