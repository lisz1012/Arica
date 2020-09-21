package com.lisz.arica.controller;

import com.github.pagehelper.PageInfo;
import com.lisz.arica.entity.Item;
import com.lisz.arica.entity.ItemHtml;
import com.lisz.arica.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
		try {
			item = itemService.add(item);
		} catch (RuntimeException e) {
			return "error";
		}
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

	/**
	 * 电商首页
	 * @param model
	 * @return
	 */
	@GetMapping("main")
	public String main(Model model, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize) {
		// List<Item> items = itemService.findAll();
		// Category参数还没传过来
		PageInfo<Item> pageInfo = itemService.findByPage(pageNum, pageSize);
		model.addAttribute("pageInfo", pageInfo);
		//return "item_main";
		return "item_page";
	}

	@GetMapping("generateMain")
	public String generateMain(Model model) {
		itemService.generateMainHtml();
		String msg = "电商首页静态页面生成成功：<a href='main.html'>查看</a>";
		model.addAttribute("msg", msg);
		return "success";
	}

	@GetMapping("health")
	public String health(Model model) {
		Map<String, Boolean> map = itemService.health();
		model.addAttribute("map", map);
		return "health";
	}

	@GetMapping("testError")
	public String testError(Model model) {
		throw new RuntimeException("出错了");
	}

	@GetMapping("edit")
	public String edit(int id, Model model) {
		Item item = itemService.getById(id);
		model.addAttribute("item", item);
		boolean hasItemEditLock = itemService.getItemEditLockForItemId(item.getId());
		model.addAttribute("hasItemEditLock", hasItemEditLock);
		return "edit";
	}

	@PostMapping("edit")
	public String edit(Item item, Model model) {
		// 这里这个item必须保证把id带过来，否则下面这句里面无法更新，为此 edit GET API返回的页面里面有个隐藏的id属性
		itemService.update(item);
		String msg = String.format("商品编辑并保存成功：<a href='item-%s.html'>查看</a>", item.getId());
		model.addAttribute("msg", msg);
		return "success";
	}

	@GetMapping("checkFile")
	public String checkFile(Model model) {
		List<Item> itemsMissingFile = itemService.getItemsMissingFile();
		model.addAttribute("itemsMissingFile", itemsMissingFile);
		return "check_file";
	}
}
