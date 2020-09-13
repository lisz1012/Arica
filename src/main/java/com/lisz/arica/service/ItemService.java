package com.lisz.arica.service;

import com.lisz.arica.entity.Item;
import com.lisz.arica.mapper.ItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}