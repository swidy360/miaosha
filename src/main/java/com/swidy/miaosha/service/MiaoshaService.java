package com.swidy.miaosha.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swidy.miaosha.domain.MiaoshaOrder;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.domain.OrderInfo;
import com.swidy.miaosha.redis.GoodsKey;
import com.swidy.miaosha.redis.MiaoshaKey;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.util.MD5Util;
import com.swidy.miaosha.util.UUIDUtil;
import com.swidy.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Autowired
	RedisService redisService;
	
	@Transactional
	public OrderInfo miaosha(GoodsVo goods, MiaoshaUser user) {
		//减库存
		boolean success = goodsService.reduceStock(goods);
		if(success){
			//生成订单
			return orderService.createOrder(goods, user);
		}else{
			setGoodsOver(goods.getId());
			return null;
		}
	}

	public void setGoodsOver(Long goodsId) {
		redisService.set(GoodsKey.isGoodsOver, ""+goodsId, true);
	}
	
	public boolean getGoodsOver(Long goodsId) {
		return redisService.exists(GoodsKey.isGoodsOver, ""+goodsId);
	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByGoodsIdUserId(goodsId, userId);
		if(order != null){
			return order.getOrderId();
		}else{
			boolean isOver = getGoodsOver(goodsId);
			if(isOver){
				return -1; //秒杀完
			}else{
				return 0; //排队中
			}
		}
	}

	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}

	public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
		if(user == null || goodsId <= 0){
			return null;
		}
		String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
		redisService.set(MiaoshaKey.getMiaoshaPath, ""+user.getId()+"_"+goodsId, path);
		return path;
	}

	public boolean checkMiaoshaPath(MiaoshaUser user, long goodsId, String path) {
		if(user == null || goodsId <= 0 || StringUtils.isEmpty(path)){
			return false;
		}
		String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath, ""+user.getId()+"_"+goodsId, String.class);
		return StringUtils.equals(oldPath, path);
	}
	
	
}
