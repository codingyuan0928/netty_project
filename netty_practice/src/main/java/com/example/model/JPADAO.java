package com.example.model;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public class JPADAO implements DAO{
	
	@Autowired
	EntityManager entityManager;

	@Override
	public Integer insert(FruitPOJO fruitPOJO) {
		Fruit fruit = new Fruit();
		fruit.setFruitName(fruitPOJO.getFruitsName());
		entityManager.persist(fruit);
		entityManager.flush();
		return fruit.getId();
	}

	@Override
	public Fruit getFruitById(Integer id) {
		return entityManager.find(Fruit.class, id);
	}

	@Override
	public Fruit update(Fruit fruit) {
		entityManager.merge(fruit);
		return fruit;
	}
	

	
}
