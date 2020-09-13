package com.lisz.arica.controller;

import com.jfinal.template.ext.spring.JFinalViewResolver;
import com.lisz.arica.EnjoyConfig;
import com.lisz.arica.entity.Item;
import com.lisz.arica.mapper.ItemDAO;
import com.lisz.arica.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller
public class MainController {
	@Autowired
	private ItemService itemService;

	@GetMapping("")
	public String index() {
		return "arica";
	}

	@GetMapping("add")
	public String add() {
		System.out.println("add!!!");
		return "add";
	}

	@PostMapping("add")
	public String add(Item item, Model model) { // 第二个参数用Model也可以，用Model.addAttribute()方法
		System.out.println("add!!!\n" + item);
		item.setLastGenerate(new Date());
		itemService.insert(item);
		model.addAttribute("msg", "Successfully added an item: " + item);
		return "success";
	}
}
