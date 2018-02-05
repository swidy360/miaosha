package com.swidy.miaosha.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.result.Result;
import com.swidy.miaosha.service.MiaoshaUserService;
import com.swidy.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	MiaoshaUserService miaoshaUserService;
	
	@RequestMapping("to_login")
	public String toLogin(){
		return "login";
	}

	@RequestMapping("do_login")
	public Result<Boolean> doLogin(@Valid LoginVo loginVo){
		if(loginVo == null) {
			return Result.error(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String password = loginVo.getPassword();
		if(StringUtils.isEmpty(mobile)){
			return Result.error(CodeMsg.MOBILE_EMPTY);
		}
		MiaoshaUser user = miaoshaUserService.getById(Long.parseLong(mobile));
		if(user == null){
			return Result.error(CodeMsg.MOBILE_NOT_EXIST);
		}
		return Result.success(true);
	}
}
