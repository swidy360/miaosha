package com.swidy.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swidy.miaosha.dao.OrderDao;
import com.swidy.miaosha.domain.MiaoshaOrder;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.domain.OrderInfo;
import com.swidy.miaosha.redis.OrderKey;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.vo.GoodsVo;

@Service
public class OrderService {

	@Autowired
	OrderDao orderDao;
	@Autowired
	RedisService redisServices;
	
	public MiaoshaOrder getMiaoshaOrderByGoodsIdUserId(Long goodsId, Long userId){
//		return orderDao.getMiaoshaOrderByGoodsIdUserId(goodsId, userId);
		return redisServices.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
	}

	public OrderInfo createOrder(GoodsVo goods, MiaoshaUser user) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setUserId(user.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsCount(1);
		orderInfo.setCreateDate(new Date());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderDao.insert(orderInfo);
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		redisServices.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goods.getId(), miaoshaOrder);
		return orderInfo;
	}

	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}

	public void deleteOrders() {
		orderDao.deleteOrders();
		orderDao.deleteMiaoshaOrders();
	}
}
