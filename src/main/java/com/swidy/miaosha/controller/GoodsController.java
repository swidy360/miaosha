package com.swidy.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.swidy.miaosha.domain.MiaoshaUser;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	
	@RequestMapping("/to_list")
	public String list(Model model,MiaoshaUser user){
		model.addAttribute("user", user);
		return "goods_list";
	}
	
}
