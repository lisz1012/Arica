package com.lisz.arica.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.ext.spring.JFinalViewResolver;
import com.lisz.arica.entity.Item;
import com.lisz.arica.entity.ItemExample;
import com.lisz.arica.entity.ItemHtml;
import com.lisz.arica.mapper.ItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

@Service
public class ItemService {
	private static final String ITEM_HTML_TEMPLATE_FILE_NAME = "item.html";

	private static final String MAIN_HTML_TEMPLATE_FILE_NAME = "item_main.html";

	//private static final String ITEM_PAGE_TEMPLATE_FILE_NAME = "item_non_static_page_template.html";

	private static final String ITEM_STATIC_PAGE_TEMPLATE_FILE_NAME = "item_static_page_template.html";

	// 只有前2页是static的，只生成前两页的静态页面文件, 因为很多人也就看前两页
	private static final int ITEM_STATIC_PAGE_COUNT = 2;

	private static final Set<Integer> ITEM_IDS_IN_EDITION = new HashSet<>();

	private static final int DEFAULT_PAGE_SIZE = 5;

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

	/**
	 * 写入数据库insert + 生成文件generateHtml，其中有一个失败的话就回滚
	 * @param item
	 * @return
	 */
	@Transactional
	public Item add(Item item) {
		item.setLastGenerate(new Date());
		try {
			itemDao.insert(item);
			generateHtml(item, ITEM_HTML_TEMPLATE_FILE_NAME);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw new RuntimeException("添加新商品失败");
		}
		return item;
	}

	/**
	 * 修改数据库updateByPrimaryKey + 生成文件generateHtml，其中有一个失败的话就回滚
	 * @param item
	 * @return
	 */
	@Transactional
	public Item update(Item item) {
		try {
			item.setLastGenerate(new Date());
			itemDao.updateByPrimaryKey(item);
			/*// item中空值的字段不会覆盖数据库中原有的值
			itemDao.updateByPrimaryKeySelective(item);*/
			generateHtml(item, ITEM_HTML_TEMPLATE_FILE_NAME);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw new RuntimeException("编辑已有商品失败");
		} finally {
			ITEM_IDS_IN_EDITION.remove(item.getId());
		}
		return item;
	}

	public synchronized boolean getItemEditLockForItemId(Integer id) {
		if (ITEM_IDS_IN_EDITION.contains(id)){
			return false;
		} else {
			ITEM_IDS_IN_EDITION.add(id);
			return true;
		}
	}

	public void releaseItemEditLock(int id) {
		ITEM_IDS_IN_EDITION.remove(id);
	}

	public void releaseAllItemEditLocks() {
		ITEM_IDS_IN_EDITION.clear();
	}

	public List<Item> getItemsMissingFile() {
		List<Item> allItems = itemDao.selectByExample(null);
		List<Item> itemsMissingFile = new ArrayList<>();
		for (Item item : allItems) {
			File file = new File(nginxRoot + "/item-" + item.getId() + ".html");
			if (!file.exists()) {
				itemsMissingFile.add(item);
			}
		}
		return itemsMissingFile;
	}

	public PageInfo<Item> findByPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Item> items = itemDao.selectByExample(null);
		return new PageInfo<>(items);
	}

	// 生成各个列表分页
	public void generateItemPages() {
		// 取出总记录条数并计算所需的页数
		long totalItemCount = itemDao.countByExample(null);
		if (totalItemCount == 0) throw new RuntimeException("商品列表为空，无法生成静态分页页面");
		long totalPages = totalItemCount / DEFAULT_PAGE_SIZE + (totalItemCount % DEFAULT_PAGE_SIZE == 0 ? 0 : 1);
		int staticItemCount = ITEM_STATIC_PAGE_COUNT * DEFAULT_PAGE_SIZE;

		Engine engine = resolver.getEngine();
		Template template = engine.getTemplate(ITEM_STATIC_PAGE_TEMPLATE_FILE_NAME);

		ItemExample example = new ItemExample();
		example.setLimit(staticItemCount);
		List<Item> staticItems = itemDao.selectByExample(example);

		// 只对前ITEM_STATIC_PAGE_COUNT页做静态页面，其中最后一页用动态模板item_page.html，因为之后的页都是动态获取了
		int pageNum = 1;
		for (; pageNum <= Math.min(ITEM_STATIC_PAGE_COUNT, totalPages); pageNum++) {
			generateItemPage(pageNum,
							 template,
					         staticItems.subList(
					         		(pageNum - 1) * DEFAULT_PAGE_SIZE,
							         Math.min(pageNum * DEFAULT_PAGE_SIZE, staticItems.size())
					         ),
							 totalPages);
		}

//		// 前几页静态 --> 动态页面无缝衔接，就不搞双向的了，回来的时候不去静态页面了，方便测试
//		if(pageNum == ITEM_STATIC_PAGE_COUNT) {
//			template = engine.getTemplate(ITEM_PAGE_TEMPLATE_FILE_NAME);
//		}
//		generateItemPage(pageNum,
//				template,
//				staticItems.subList(
//						(pageNum - 1) * DEFAULT_PAGE_SIZE,
//						Math.min(pageNum * DEFAULT_PAGE_SIZE, staticItems.size())
//				),
//				totalPages);
	}

	private void generateItemPage(int pageNum, Template template, List<Item> items, long totalPages) {
		Kv kv = Kv.by("items", items);
		kv.set("totalPages", totalPages);
		kv.set("currentPageNum", pageNum);
		kv.set("staticPages", ITEM_STATIC_PAGE_COUNT);
		String fileName = String.format("item_page-%s.html", pageNum);
		String filePath = nginxRoot;

		String fullPath = filePath + "/" + fileName;
		template.render(kv, fullPath);
	}
}