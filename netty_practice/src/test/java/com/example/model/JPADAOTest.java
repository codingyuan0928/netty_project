package com.example.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class JPADAOTest {

	@Autowired
	private JPADAO dao;

	@Test
	public void getFruitById() {
		// 先插入一筆資料
		FruitPOJO fruitPOJO = new FruitPOJO();
		fruitPOJO.setFruitsName("蘋果");
		Integer id = dao.insert(fruitPOJO);

		// 測試getFruitById是否能找到剛剛插入的資料
		Fruit fruit = dao.getFruitById(id);

		assertNotNull(fruit);
		assertEquals(id, fruit.getId());
		assertEquals("蘋果", fruit.getFruitName());
	}

	@Test
	public void insert() {

		FruitPOJO fruitPOJO = new FruitPOJO();
		fruitPOJO.setFruitsName("芒果");
		Integer id = dao.insert(fruitPOJO);

		assertNotNull(id);
	}

	@Test
	public void update() {

		// 先插入一筆資料
		FruitPOJO fruitPOJO = new FruitPOJO();
		fruitPOJO.setFruitsName("蘋果");
		Integer id = dao.insert(fruitPOJO);

		Fruit fruit = dao.getFruitById(id);

		fruit.setFruitName("香蕉");
		Fruit updatedFruit = dao.update(fruit);

		assertNotNull(updatedFruit);
		assertEquals(id, updatedFruit.getId());
		assertEquals("香蕉", updatedFruit.getFruitName());

	}

}
