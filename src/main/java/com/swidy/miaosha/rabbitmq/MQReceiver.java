package com.swidy.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swidy.miaosha.domain.MiaoshaOrder;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.service.GoodsService;
import com.swidy.miaosha.service.MiaoshaService;
import com.swidy.miaosha.service.OrderService;
import com.swidy.miaosha.vo.GoodsVo;

@Service
public class MQReceiver {
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Autowired
	MiaoshaService miaoshaService;

	private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);
	
	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
	public void receiveMiaoshaQueue(String message){
		logger.info("receive miaosha message:" + message);
		MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
		MiaoshaUser user = mm.getUser();
		long goodsId = mm.getGoodsId();
		
		//判断数据库商品库存是否足够
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stocks = goods.getStockCount();
		if(stocks <= 0){
			return;
		}
		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByGoodsIdUserId(goodsId, user.getId());
		if(order != null){
			return;
		}
		//减库存 下订单 写入秒杀单
		miaoshaService.miaosha(goods, user);
	}
	
	@RabbitListener(queues=MQConfig.QUEUE)
	public void receive(String message){
		logger.info("receive message:" + message);
	}
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
	public void receiveTopic1(String message){
		logger.info("receive queue1 message:" + message);
	}
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
	public void receiveTopic2(String message){
		logger.info("receive queue2 message:" + message);
	}
	
	@RabbitListener(queues=MQConfig.HEADER_QUEUE)
	public void receiveHeader(byte[] message){
		logger.info("receive header message:" + new String(message));
	}
	
}
