package com.swidy.miaosha.exception;

import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swidy.miaosha.result.CodeMsg;
import com.swidy.miaosha.result.Result;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value=Exception.class)
	public Result<String> exceptionHandler(Exception e){
		e.printStackTrace();
		if(e instanceof GlobalException){
			GlobalException ex = (GlobalException) e;
			return Result.error(ex.getCm());
		}else if(e instanceof BindException){
			BindException ex = (BindException)e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String message = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArg(message));
		}
		return Result.error(CodeMsg.SERVER_ERROR);
	}
}
