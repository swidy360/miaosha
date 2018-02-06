package com.swidy.miaosha.result;


public class CodeMsg {
	
	private int code;
	private String msg;
	
	//通用异常 5001**
	public static CodeMsg SUCCESS = new CodeMsg(0,"success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务器端异常");
	//登录模块 5002XX
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500200,"手机号码不能为空");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500201,"密码不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500202,"手机号码格式错误");
	public static CodeMsg MOBILE_NOT_EXIST= new CodeMsg(500202,"手机号码不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500203,"密码错误");
	
	
	//商品模块 5003XX
	
	//订单模块 5004XX
	
	//秒杀模块 5005XX
	
	
	public CodeMsg(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}