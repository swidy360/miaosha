package com.swidy.miaosha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.service.GoodsService;
import com.swidy.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	@Autowired
	GoodsService goodsService;
	
	@RequestMapping("/to_list")
	public String list(Model model,MiaoshaUser user){
		model.addAttribute("user", user);
		//查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsList);
		return "goods_list";
	}
	
	@RequestMapping("/to_detail/{goodsId}")
	public String detail(Model model,MiaoshaUser user,@PathVariable("goodsId")Long goodsId){
		model.addAttribute("user", user);
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);
		
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		int remainSeconds = 0;
		int miaoshaStatus = 0; 
		if(now < startAt){ //秒杀未开始
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) /1000);
		}else if(now > endAt){ //秒杀已结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else{ //秒杀正在进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("remainSeconds", remainSeconds);
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		return "goods_detail";
	}
	
}
