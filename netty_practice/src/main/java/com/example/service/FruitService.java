package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DTO.FruitDTO;
import com.example.model.DAO;
import com.example.model.Fruit;
import com.example.model.FruitPOJO;

@Service
public class FruitService {

	@Autowired
	DAO dao;

	public Integer createFruit(FruitPOJO fruitPOJO) {
		return dao.insert(fruitPOJO);
	}

	public FruitDTO getFruitById(Integer id) {
		FruitDTO fruitDTO = new FruitDTO();
		Fruit fruit = dao.getFruitById(id);
		fruitDTO.setId(fruit.getId());
		fruitDTO.setFruitName(fruit.getFruitName());
		return fruitDTO;
	}

	public FruitDTO updateExistingFruitData(FruitDTO fruitDTO) {
		Fruit fruit = dao.getFruitById(fruitDTO.getId());
		if (fruit == null) {
			return null;
		}
		fruit.setFruitName(fruitDTO.getFruitName());
		Fruit updatedFruit = dao.update(fruit);
		return new FruitDTO(updatedFruit.getId(), updatedFruit.getFruitName());
	}
}
