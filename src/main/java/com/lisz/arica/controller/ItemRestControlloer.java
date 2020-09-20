package com.lisz.arica.controller;

import com.lisz.arica.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemRestControlloer {
	@Autowired
	private ItemService itemService;

	@GetMapping("releaseItemEditLock")
	public void releaseItemEditLock(int id) {
		System.out.println("releasing ... ");
		itemService.releaseItemEditLock(id);
	}

	@GetMapping("releaseAllItemEditLocks")
	public void releaseAllItemEditLocks() {
		System.out.println("releasing all locks ... ");
		itemService.releaseAllItemEditLocks();
	}
}
