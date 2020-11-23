package com.sutpc.demo.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sutpc.demo.model.HttpResult;


@ControllerAdvice
public class ControllerException {
	
	//统一异常
	private Logger logger = LogManager.getLogger(ControllerException.class);

	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public HttpResult errorHandler(Exception ex) {

		logger.error(ex.getMessage()+getStackMsg(ex));		

		return HttpResult.error(ex.getMessage());
	}

	private String getStackMsg(Exception e) {

		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stackArray = e.getStackTrace();
		for (int i = 0; i < stackArray.length; i++) {
			StackTraceElement element = stackArray[i];
			sb.append(element.toString() + "\n");
		}
		return sb.toString();
	}
	
}
