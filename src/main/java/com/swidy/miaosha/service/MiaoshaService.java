package com.swidy.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.domain.OrderInfo;
import com.swidy.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	
	public OrderInfo miaosha(GoodsVo goods, MiaoshaUser user) {
		//减库存
		goodsService.reduceStock(goods);
		//生成订单
		return orderService.createOrder(goods, user);
	}
	
	
}
