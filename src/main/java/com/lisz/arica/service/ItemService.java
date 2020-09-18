package com.lisz.arica.service;

import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.ext.spring.JFinalViewResolver;
import com.jfinal.template.source.FileSourceFactory;
import com.lisz.arica.entity.Item;
import com.lisz.arica.entity.ItemHtml;
import com.lisz.arica.mapper.ItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
	private static final String ITEM_HTML_TEMPLATE_FILE_NAME = "item.html";

	@Autowired
	private ItemDAO itemDao;

	@Value("${nginx.html.root}")
	private String nginxRoot;

	@Value("${nginx.template.path}")
	private String templatePath;

	@Autowired
	private JFinalViewResolver resolver;

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

	public void generateHtml(int id) {
		Engine engine = resolver.getEngine();
		Template template = engine.getTemplate(ITEM_HTML_TEMPLATE_FILE_NAME);
		// 从数据源获取数据
		Item item = itemDao.selectByPrimaryKey(id);
		generateHtml(item, template);
	}

	private void generateHtml(Item item, Template template) {
		Kv kv = Kv.by("item", item);
		String fileName = "item-" + item.getId() + ".html";
		String filePath = nginxRoot;
		// 最后会修改这个路径
		String fullPath = filePath + "/" + fileName;
		template.render(kv, fullPath);
		if (item instanceof ItemHtml) {
			((ItemHtml)item).setLocation(fullPath);
		}

	}

	public String getFileTemplateAsString() {
		StringBuffer sb = new StringBuffer();
		try (InputStream in = new FileInputStream(templatePath + "/" + ITEM_HTML_TEMPLATE_FILE_NAME);
		     BufferedReader br = new BufferedReader(new InputStreamReader(in));) {

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public void saveTemplate(String content) {
		String fileName = templatePath + "/" + ITEM_HTML_TEMPLATE_FILE_NAME; //ClassUtils.getDefaultClassLoader().getResource("templates_1/item.html").getFile();
		try (FileWriter fw = new FileWriter(fileName, false);) {
			fw.write(content);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<ItemHtml> generateAll() {
		Engine engine = resolver.getEngine();
		Template template = engine.getTemplate(ITEM_HTML_TEMPLATE_FILE_NAME);
		List<ItemHtml> itemHtmls = itemDao.selectAll();
		for (ItemHtml itemHtml : itemHtmls) {
			try {
				generateHtml(itemHtml, template);
				itemHtml.setHtmlGenerateStatus("Success");
			} catch (Exception e) {
				itemHtml.setHtmlGenerateStatus("Failed");
				e.printStackTrace();
			}
		}
		return itemHtmls;
	}
}