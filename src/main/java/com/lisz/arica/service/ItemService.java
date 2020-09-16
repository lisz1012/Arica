package com.lisz.arica.service;

import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.lisz.arica.entity.Item;
import com.lisz.arica.mapper.ItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.util.List;

@Service
public class ItemService {
	@Autowired
	private ItemDAO itemDao;

	@Value("${nginx.html.root}")
	private String nginxRoot;

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
		String filePath = nginxRoot;
		// 最后会修改这个路径
		template.render(kv, filePath + "/" + fileName);
	}

	public String getFileTemplateAsString() {
		StringBuffer sb = new StringBuffer();
		try (InputStream in = ClassUtils.getDefaultClassLoader().getResourceAsStream("templates/item.html");
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
		String fileName = ClassUtils.getDefaultClassLoader().getResource("templates/item.html").getFile();
		try (FileWriter fw = new FileWriter(fileName, false);) {
			fw.write(content);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}