package com.swidy.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swidy.miaosha.dao.MiaoshaUserDao;
import com.swidy.miaosha.domain.MiaoshaUser;

@Service
public class MiaoshaUserService {
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	public MiaoshaUser getById(long id){
		return miaoshaUserDao.getById(id);
	}

}
