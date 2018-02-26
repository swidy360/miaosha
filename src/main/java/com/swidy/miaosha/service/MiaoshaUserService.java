package com.swidy.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.swidy.miaosha.dao.MiaoshaUserDao;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.exception.GlobalException;
import com.swidy.miaosha.redis.MiaoshaUserKey;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.util.MD5Util;
import com.swidy.miaosha.util.UUIDUtil;
import com.swidy.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {
	
	public static final String COOKIE_NAME_TOKEN = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaUser getById(long id){
		return miaoshaUserDao.getById(id);
	}

	public boolean login(HttpServletResponse response, LoginVo loginVo) {
		if(loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		if(StringUtils.isEmpty(mobile)){
			throw new GlobalException(CodeMsg.MOBILE_EMPTY);
		}
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user == null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		String salt = user.getSalt();
		String dbPass = user.getPassword();
		String calcPass = MD5Util.formPassToDBPass(formPass, salt);
		if(!calcPass.equals(dbPass)){
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		} 
		String token = UUIDUtil.uuid();
		addCookie(response, token, user);
		return true;
	}

	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
		cookie.setPath("/");
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		response.addCookie(cookie);
	}

	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		if(user != null) {
			addCookie(response, token, user); 
		}
		return user;
	}

}
