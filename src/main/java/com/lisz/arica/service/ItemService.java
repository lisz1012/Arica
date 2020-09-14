package com.lisz.arica.service;

import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.lisz.arica.entity.Item;
import com.lisz.arica.mapper.ItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
	@Autowired
	private ItemDAO itemDao;

	public Item  insert(Item item) {
		itemDao.insert(item);
		return item;
	}

	public Item getById(int id) {
		return itemDao.selectByPrimaryKey(id);
	}

	public List<Item> findAll() {
		return itemDao.selectByExample(null);
	}

	public void generageHtml(int id) {
		Engine engine = Engine.use();
		engine.setDevMode(true);
		engine.setToClassPathSourceFactory();
		Template template = engine.getTemplate("templates/item.html");
		// 从数据源获取数据
		Item item = itemDao.selectByPrimaryKey(id);
		Kv kv = Kv.by("item", item);
		String fileName = "item-" + id + ".html";
		String filePath = "/Users/shuzheng/Documents/dev/uploads";
		// 最后会修改这个路径
		template.render(kv, filePath + "/" + fileName);
	}
}