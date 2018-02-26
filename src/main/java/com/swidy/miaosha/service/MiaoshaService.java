package com.swidy.miaosha.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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

	public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
		if(user == null || goodsId <= 0){
			return null;
		}
		int width = 80;
		int height = 32;
		//create the imag
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();
		//set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		//draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width-1, height-1);
		//create a random instance to generate the codes
		Random rdm = new Random();
		//make some confusion
		for(int i=0; i < 50; i++){
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		//generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0,100,0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = (int) calc(verifyCode);
		redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
		return image;
	}

	private static double calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return  (Double) engine.eval(exp);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static char[] ops = new char[]{'+','-','*'};
	/**
	 * + - * 
	 */
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
		int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = "" + num1 + op1 + num2 + op2 + num3;
		return exp;
	}

	public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
		if(user == null || goodsId <= 0){
			return false;
		}
		Integer codeOld = Integer.valueOf(redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, String.class));
		if(codeOld == null || codeOld - verifyCode != 0){
			return false;
		}
		redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
		return true;
	}
	
	
}
