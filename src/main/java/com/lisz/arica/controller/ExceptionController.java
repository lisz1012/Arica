package com.lisz.arica.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Controller里的方法如果抛出来了异常，就在这里会接收到
public class ExceptionController {

	@ExceptionHandler(Exception.class) // 按照异常的种类处理
	public String defaultException(Exception e, Model model) {
		System.out.println("------ error" + e.getMessage());
		model.addAttribute("msg", e.getMessage());
		return "error";
	}
}
