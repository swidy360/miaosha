package com.swidy.miaosha.result;


public class CodeMsg {
	
	private int code;
	private String msg;
	
	//通用异常 5001**
	public static CodeMsg SUCCESS = new CodeMsg(0,"success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务器端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
	public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "非法请求");
	//登录模块 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500200,"手机号码不能为空");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500201,"密码不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500202,"手机号码格式错误");
	public static CodeMsg MOBILE_NOT_EXIST= new CodeMsg(500202,"手机号码不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500203,"密码错误");
	
	
	//商品模块 5003XX
	
	//订单模块 5004XX
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在");
	
	//秒杀模块 5005XX
	public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
	public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复秒杀");
	
	
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
	
	public CodeMsg fillArg(Object...args){
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code,message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}
}
