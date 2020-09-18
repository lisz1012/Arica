package com.lisz.arica.controller;

import com.lisz.arica.entity.Item;
import com.lisz.arica.entity.ItemHtml;
import com.lisz.arica.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@GetMapping("item")
	public String preview(@RequestParam int id, Model model) {
		Item item = itemService.getById(id);
		model.addAttribute("item", item);
		System.out.println("Item Loaded: " + item);
		return "item";
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
		System.out.println("Items Loaded: " + items);
		return "item_list";
	}

	/**
	 * item 列表，可以生成html文件、修改item信息
	 * @param model
	 * @return
	 */
	@GetMapping("generageHtml")
	public String generageHtml(int id, Model model) {
		itemService.generateHtml(id);
		String msg = String.format("文件生成成功，<a href='item-%s.html'>预览</a>", id);
		model.addAttribute("msg", msg);
		return "success";
	}

	/**
	 * 模板管理
	 * @param model
	 * @return
	 */
	@GetMapping("templates")
	public String templates(Model model) {

		return "templates";
	}

	/**
	 * 修改模板
	 * @param model
	 * @return
	 */
	@GetMapping("editTemplate")
	public String editTemplate(Model model) {
		String templateStr = itemService.getFileTemplateAsString();
		model.addAttribute("templateStr", templateStr);
		return "edit_template";
	}

	/**
	 * 保存模板
	 * @return
	 */
	@PostMapping("saveTemplate")
	public String saveTemplate(Model model, String content) {
		itemService.saveTemplate(content);
		String msg = "模板修改成功";
		model.addAttribute("msg", msg);
		return "success";
	}

	/**
	 *
	 * @param model
	 * @return
	 */
	@GetMapping("generateAll")
	public String generateAll(Model model) {
		List<ItemHtml> itemHtmls = itemService.generateAll();
		model.addAttribute("result", itemHtmls);
		return "generateAll";
	}
}
