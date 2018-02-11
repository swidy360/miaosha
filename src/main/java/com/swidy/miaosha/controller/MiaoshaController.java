package com.swidy.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.swidy.miaosha.domain.MiaoshaOrder;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.domain.OrderInfo;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.service.GoodsService;
import com.swidy.miaosha.service.MiaoshaService;
import com.swidy.miaosha.service.OrderService;
import com.swidy.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Autowired
	MiaoshaService miaoshaService;
	
	
	@RequestMapping(value="/do_miaosha", method=RequestMethod.POST)
	public String miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId")Long goodsId){
		model.addAttribute("user", user);
		if(user == null){
			return "login";
		}
		//判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0){
			model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
			return "miaosha_fail";
		}
		//判断是否已经秒杀到了
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByGoodsIdUserId(goodsId, user.getId());
		if(miaoshaOrder != null){
			model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
			return "miaosha_fail";
		}
		//减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(goods, user);
		model.addAttribute("orderInfo", orderInfo);
		model.addAttribute("goods", goods);
		return "order_detail";
	}
}
