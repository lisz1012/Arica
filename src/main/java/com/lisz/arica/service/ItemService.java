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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {
	private static final String ITEM_HTML_TEMPLATE_FILE_NAME = "item.html";

	private static final String MAIN_HTML_TEMPLATE_FILE_NAME = "item_main.html";

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
		// 从数据源获取数据
		Item item = itemDao.selectByPrimaryKey(id);
		generateHtml(item, ITEM_HTML_TEMPLATE_FILE_NAME);
	}

	private void generateHtml(Item item, String templateFileName) {
		Engine engine = resolver.getEngine();
		Template template = engine.getTemplate(templateFileName);
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
		List<ItemHtml> itemHtmls = itemDao.selectAll();
		for (ItemHtml itemHtml : itemHtmls) {
			try {
				generateHtml(itemHtml, ITEM_HTML_TEMPLATE_FILE_NAME);
				itemHtml.setHtmlGenerateStatus("Success");
			} catch (Exception e) {
				itemHtml.setHtmlGenerateStatus("Failed");
				e.printStackTrace();
			}
		}
		return itemHtmls;
	}

	public void generateMainHtml() {
		Engine engine = resolver.getEngine();
		Template template = engine.getTemplate(MAIN_HTML_TEMPLATE_FILE_NAME);
		List<Item> items = itemDao.selectByExample(null);
		Kv kv = Kv.by("items", items);
		String fileName = "main.html";
		String filePath = nginxRoot;
		// 最后会修改这个路径
		String fullPath = filePath + "/" + fileName;
		template.render(kv, fullPath);
	}

	public Map<String, Boolean> health() {
		Map<String, Boolean> map = new HashMap<>();
		map.put("192.168.1.2", null);
		map.put("192.168.1.254", null);
		map.put("192.168.1.33", null);

		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			try {
				InetAddress inetAddress = InetAddress.getByName(entry.getKey());
				entry.setValue(inetAddress.isReachable(3000));
//				if () {
//					entry.setValue(true);
//				} else {
//					entry.setValue(false);
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}