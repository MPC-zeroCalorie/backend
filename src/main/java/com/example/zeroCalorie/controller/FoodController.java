package com.example.zeroCalorie.controller;

import com.example.zeroCalorie.dto.FoodDTO;
import com.example.zeroCalorie.entity.Food;
import com.example.zeroCalorie.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    // 음식 데이터 저장 API
    @PostMapping("/save")
    public ResponseEntity<FoodDTO> saveFood(
            @RequestParam String email,
            @RequestBody FoodDTO foodDTO) {
        FoodDTO savedFood = foodService.saveFood(email, foodDTO);
        return ResponseEntity.ok(savedFood);
    }

    // 직접 Food 객체 추가 API
    @PostMapping("/add")
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        Food savedFood = foodService.saveFood(food);
        return new ResponseEntity<>(savedFood, HttpStatus.CREATED); // 상태 코드를 201로 설
//        return ResponseEntity.ok(savedFood);
    }

    // 일간 음식 기록 조회 API
    @GetMapping("/daily")
    public ResponseEntity<List<FoodDTO>> getDailyRecords(
            @RequestParam String email,
            @RequestParam LocalDate date) {
        List<FoodDTO> records = foodService.getDailyRecords(email, date);
        return ResponseEntity.ok(records);
    }

    // 주간 음식 기록 조회 API
    @GetMapping("/weekly")
    public ResponseEntity<List<FoodDTO>> getWeeklyRecords(
            @RequestParam String email,
            @RequestParam int year,
            @RequestParam int week) {
        List<FoodDTO> records = foodService.getWeeklyRecords(email, year, week);
        return ResponseEntity.ok(records);
    }

    // 월간 음식 기록 조회 API
    @GetMapping("/monthly")
    public ResponseEntity<List<FoodDTO>> getMonthlyRecords(
            @RequestParam String email,
            @RequestParam int year,
            @RequestParam int month) {
        List<FoodDTO> records = foodService.getMonthlyRecords(email, year, month);
        return ResponseEntity.ok(records);
    }

    // 음식 데이터 삭제 API
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
