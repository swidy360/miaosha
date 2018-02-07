package com.swidy.miaosha.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swidy.miaosha.result.Result;
import com.swidy.miaosha.service.MiaoshaUserService;
import com.swidy.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	MiaoshaUserService userService;
	
	@RequestMapping("to_login")
	public String toLogin(){
		return "login";
	}

	@RequestMapping("do_login")
	@ResponseBody
	public Result<Boolean> doLogin(@Valid LoginVo loginVo){
		userService.login(loginVo);
		return Result.success(true);
	}
}
