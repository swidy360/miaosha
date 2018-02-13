package com.swidy.miaosha.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swidy.miaosha.domain.MiaoshaOrder;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.rabbitmq.MQSender;
import com.swidy.miaosha.rabbitmq.MiaoshaMessage;
import com.swidy.miaosha.redis.GoodsKey;
import com.swidy.miaosha.redis.OrderKey;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.result.Result;
import com.swidy.miaosha.service.GoodsService;
import com.swidy.miaosha.service.MiaoshaService;
import com.swidy.miaosha.service.OrderService;
import com.swidy.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Autowired
	MiaoshaService miaoshaService;
	@Autowired
	RedisService redisService;
	@Autowired
	MQSender sender;
	
	private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();
	
	/**
	 * 系统初始化：商品库存加载到缓存
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null){
			return;
		}
		for(GoodsVo goods : goodsList){
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}
	
	@RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(MiaoshaUser user, @RequestParam("goodsId")long goodsId,
			@PathVariable(value="path")String path){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		boolean check = miaoshaService.checkMiaoshaPath(user,goodsId,path);
		if(!check){
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		//内存标记 减少redis访问
		boolean over = localOverMap.get(goodsId);
		if(over){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByGoodsIdUserId(goodsId, user.getId());
		if(miaoshaOrder != null){
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//预减库存
		Long stocks = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);
		if(stocks < 0){
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//入队列
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setGoodsId(goodsId);
		mm.setUser(user);
		sender.sendMiaoshaMessage(mm);
		return Result.success(0); //排队中
		/*//判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByGoodsIdUserId(goodsId, user.getId());
		if(miaoshaOrder != null){
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(goods, user); 
		return Result.success(orderInfo);*/
	}
	
	
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> getMiaoshaResult(MiaoshaUser user, @RequestParam("goodsId")long goodsId){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
		return Result.success(result);
	}
	
	@RequestMapping(value="/path", method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaPath(MiaoshaUser user, @RequestParam("goodsId")long goodsId){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		String path = miaoshaService.createMiaoshaPath(user,goodsId);
		return Result.success(path);
	}
	
	@RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
		redisService.delete(GoodsKey.isGoodsOver); 
		miaoshaService.reset(goodsList);
		return Result.success(true);
	}
	
	
	

}
