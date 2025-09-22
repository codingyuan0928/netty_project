package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.model.FruitPOJO;
import com.example.service.FruitService;
import com.example.DTO.FruitDTO;
import com.example.DTO.ResponseDTO;

import java.util.Optional;

@RequestMapping("/api")
@RestController
public class FruitController {
    @Autowired
    private FruitService fruitService;

    @PostMapping("/fruits")
    public ResponseEntity<ResponseDTO<Integer>> insertData(@RequestBody FruitPOJO fruitPOJO) {
        Integer id = fruitService.createFruit(fruitPOJO);
        return ResponseEntity.ok(ResponseDTO.success(id));
    }

    @GetMapping("/fruits/{id}")
    public ResponseEntity<ResponseDTO<FruitDTO>> getFruitById(@PathVariable Integer id) {
        return Optional.ofNullable(fruitService.getFruitById(id))
                .map(fruit -> ResponseEntity.ok(ResponseDTO.success(fruit)))
                .orElseGet(() -> ResponseEntity.ok(ResponseDTO.error("Fruit not found")));
    }

    @PutMapping("/fruits")
    public ResponseEntity<ResponseDTO<FruitDTO>> updateFruitData(@RequestBody FruitDTO fruitDTO) {
        return Optional.ofNullable(fruitService.updateExistingFruitData(fruitDTO))
                .map(updatedFruit -> ResponseEntity.ok(ResponseDTO.success(updatedFruit)))
                .orElseGet(() -> ResponseEntity.ok(ResponseDTO.error("Fruit update failed or not found")));
    }
}
