package com.swidy.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.alibaba.druid.util.StringUtils;
import com.swidy.miaosha.domain.MiaoshaUser;
import com.swidy.miaosha.redis.GoodsKey;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.result.Result;
import com.swidy.miaosha.service.GoodsService;
import com.swidy.miaosha.vo.GoodsDetailVo;
import com.swidy.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	
	Logger logger = LoggerFactory.getLogger(GoodsController.class);
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;	
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	RedisService redisService;	
	
	@RequestMapping("/to_list2")
	public String list2(Model model,MiaoshaUser user){
		model.addAttribute("user", user);
		//查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsList);
		return "goods_list";
	}
	
	@RequestMapping(value="/to_list", produces="text/html")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user){
		model.addAttribute("user", user);
		//取缓存
		String html = redisService.get(GoodsKey.getGoodList, "", String.class);
		if(!StringUtils.isEmpty(html)){
			logger.info("取商品列表缓存");
			return html;
		}
		//查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsList);
		
		SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), 
				request.getLocale(), model.asMap(), applicationContext);
		//手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);		
		if(!StringUtils.isEmpty(html)){
			redisService.set(GoodsKey.getGoodList, "", html);
		}
		logger.info("取商品列表数据库");
		return html;
	}
	
	@RequestMapping("/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model,
			MiaoshaUser user,@PathVariable("goodsId")long goodsId){
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
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
		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setMiaoshaStatus(miaoshaStatus);
		vo.setRemainSeconds(remainSeconds);
		return Result.success(vo);
	}
	
	@RequestMapping(value="/to_detail2/{goodsId}", produces="text/html")
	public String detail2(Model model,MiaoshaUser user,@PathVariable("goodsId")long goodsId){
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
