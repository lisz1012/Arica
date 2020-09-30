package com.lisz.arica.service;

import com.lisz.arica.entity.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemServiceTest {
	@Autowired
	private ItemService itemService;

	@Test
	public void testGetById(){
		int id = 1;
		Item item = itemService.getById(id);
		Assertions.assertNotNull(item);
		Assertions.assertEquals(1, item.getId());
	}

	@Test
	public void testGetById2(){
		int id = -100;
		Item item = itemService.getById(id);
		Assertions.assertNull(item);
	}
}
