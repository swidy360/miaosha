package com.swidy.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.domain.OrderInfo;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.result.Result;
import com.swidy.miaosha.service.GoodsService;
import com.swidy.miaosha.service.OrderService;
import com.swidy.miaosha.vo.GoodsVo;
import com.swidy.miaosha.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	OrderService orderService;
	@Autowired
	GoodsService goodsService;

	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> info(Model model,MiaoshaUser user,//@PathVariable("orderId")long orderId
			@RequestParam("orderId") long orderId){
		if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
		OrderInfo order = orderService.getOrderById(orderId);
		if(order == null){
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		long goodsId = order.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo vo = new OrderDetailVo();
		vo.setGoods(goods);
		vo.setOrder(order);
		return Result.success(vo);
	}
}
