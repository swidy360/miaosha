package com.swidy.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swidy.miaosha.domain.User;
import com.swidy.miaosha.rabbitmq.MQSender;
import com.swidy.miaosha.redis.RedisService;
import com.swidy.miaosha.redis.UserKey;
import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	MQSender sender;
	
	@RequestMapping("/mq")
	@ResponseBody
	public String queue(){
		sender.send("hello world");
		return "hello world";
	}
	
	@RequestMapping("/mq/topic")
	@ResponseBody
	public String topic(){
		sender.topicSend("hello topic world");
		return "hello topic world";
	}
	
	@RequestMapping("/mq/fanout")
	@ResponseBody
	public String fanout(){
		sender.fanoutSend("hello fanout world");
		return "hello fanout world";
	}
	
	@RequestMapping("/mq/header")
	@ResponseBody
	public String header(){
		sender.headerSend("hello header world");
		return "hello header world";
	}
	
	@RequestMapping("/")
	@ResponseBody
	public String home(){
		return "hello world";
	}
	
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello(){
		return Result.success("success");
	}
	
	@RequestMapping("/helloerror")
	@ResponseBody()
	public Result<CodeMsg> helloError(){
		return Result.error(CodeMsg.SERVER_ERROR);
	}
	
	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model){
		model.addAttribute("name", "swidy");
		return "hello";
	}
	
	@RequestMapping("/set_redis")
	@ResponseBody
	public Result<Boolean> setRedis(){
		User user = new User();
		user.setId("1");
		user.setName("swidy360");
		redisService.set(UserKey.getById,"1", user);
		return Result.success(true);
	}
	
	@RequestMapping("/get_redis")
	@ResponseBody
	public Result<User> getRedis(){
		User user = redisService.get(UserKey.getById,"1", User.class);
		return Result.success(user);
	}
	

}
