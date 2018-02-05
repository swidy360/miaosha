package com.swidy.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swidy.result.CodeMsg;
import com.swidy.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@RequestMapping("/")
	@ResponseBody
	public String home(){
		return "hello world";
	}
	
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello(){
		return Result.success("success");
	}
	
	@RequestMapping("/helloerror")
	@ResponseBody()
	public Result<CodeMsg> helloError(){
		return Result.error(CodeMsg.SERVER_ERROR);
	}
	
	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model){
		model.addAttribute("name", "swidy");
		return "hello";
	}

}
