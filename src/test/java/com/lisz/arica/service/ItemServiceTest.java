package com.lisz.arica.service;

import com.lisz.arica.entity.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemServiceTest {

	@Test
	public void testGetById(){
		System.out.println("Running testGetById");
	}

	@Test
	public void testGenerateHtml(){
		System.out.println("Running testGenerateHtml");
	}

	@Test
	public void testGetFileTemplateAsString(){
		System.out.println("Running testGetFileTemplateAsString");
	}
}
