package com.example.model;



public interface DAO {
	Integer insert(FruitPOJO fruit);
	Fruit getFruitById(Integer id);
	Fruit update(Fruit fruit);
}
