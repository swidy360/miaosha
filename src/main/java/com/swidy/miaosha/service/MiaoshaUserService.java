package com.swidy.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.swidy.miaosha.dao.MiaoshaUserDao;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.exception.GlobalException;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.util.MD5Util;
import com.swidy.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	public MiaoshaUser getById(long id){
		return miaoshaUserDao.getById(id);
	}

	public boolean login(LoginVo loginVo) {
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
		return true;
	}

}
