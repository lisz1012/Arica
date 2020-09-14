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
import java.util.List;

@Controller
public class MainController {
	@Autowired
	private ItemService itemService;

	@GetMapping("")
	public String index() {
		return "arica";
	}

	/**
	 * 进入"发布Item"页面
	 * @return
	 */
	@GetMapping("add")
	public String add() {
		System.out.println("add!!!");
		return "add";
	}

	/**
	 * 表单接收，新商品入库
	 * @param item
	 * @param model
	 * @return
	 */
	@PostMapping("add")
	public String add(Item item, Model model) { // 第二个参数用Model也可以，用Model.addAttribute()方法
		item.setLastGenerate(new Date());
		item = itemService.insert(item);
		model.addAttribute("msg", "Successfully added an item: ");
		model.addAttribute("item", item);
		System.out.println("add!!!\n" + item);
		return "success";
	}

	/**
	 * 预览刚刚入库的商品，临时预览，动态
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("preview")
	public String preview(@RequestParam int id, Model model) {
		Item item = itemService.getById(id);
		model.addAttribute("item", item);
		System.out.println("Item Loaded: " + item);
		return "preview";
	}

	/**
	 * item 列表，可以生成html文件、修改item信息
	 * @param model
	 * @return
	 */
	@GetMapping("itemList")
	public String itemList(Model model) {
		List<Item> items = itemService.findAll();
		model.addAttribute("items", items);
		System.out.println("Item Loaded: " + items);
		return "item_list";
	}
}
