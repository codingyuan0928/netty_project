package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.DTO.FruitDTO;
import com.example.model.DAO;
import com.example.model.Fruit;
import com.example.model.FruitPOJO;

@ExtendWith(MockitoExtension.class) // 使用Mockito，不啟動Spring Boot
public class FruitServiceTest {

	@InjectMocks
	private FruitService fruitService; // 測試對象

	@Mock
	private DAO dao; // Mock dao

	private FruitPOJO fruitPOJO;
	private Fruit mockFruit;
	private Fruit mockUpdatedFruit;
	@BeforeEach
	public void initTest() {

		fruitPOJO = new FruitPOJO();
		fruitPOJO.setFruitsName("荔枝");

		mockFruit = new Fruit();
		mockFruit.setId(1);
		mockFruit.setFruitName("荔枝");
		
		mockUpdatedFruit = new Fruit();
		mockUpdatedFruit.setId(1);
		mockUpdatedFruit.setFruitName("香蕉");

	}

	@Test
	public void createFruit() {

		when(dao.insert(any(FruitPOJO.class))).thenReturn(1);

		Integer id = fruitService.createFruit(fruitPOJO);

		assertNotNull(id);
		assertEquals(1, id);
	}

	@Test
	public void getFruitById() {
		when(dao.getFruitById(eq(1))).thenReturn(mockFruit);

		FruitDTO fruitDTO = fruitService.getFruitById(1);
		
		assertNotNull(fruitDTO);
		assertEquals(1, fruitDTO.getId());
		assertEquals("荔枝", fruitDTO.getFruitName());
	}
	@Test
	public void updateExistingFruitData() {
		
		when(dao.getFruitById(eq(1))).thenReturn(mockFruit);
		
		when(dao.update(any(Fruit.class))).thenReturn(mockUpdatedFruit);
		
		FruitDTO fruitDTO = fruitService.getFruitById(1);
		
		FruitDTO updatedFruitDTO = fruitService.updateExistingFruitData(fruitDTO);
		
		assertNotNull(updatedFruitDTO);
		assertEquals(1,updatedFruitDTO.getId());
		assertEquals("香蕉",updatedFruitDTO.getFruitName());
		
		
	}
}
