package com.example.zeroCalorie.controller;

import com.example.zeroCalorie.dto.FoodImageDTO;
import com.example.zeroCalorie.service.FoodImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-image")
@RequiredArgsConstructor
public class FoodImageController {

    private final FoodImageService foodImageService;

    // 음식 이미지 저장 API
    @PostMapping("/save")
    public ResponseEntity<FoodImageDTO> saveFoodImage(
            @RequestParam String email,
            @RequestBody FoodImageDTO foodImageDTO) {
        FoodImageDTO savedImage = foodImageService.saveFoodImage(email, foodImageDTO);
        return ResponseEntity.ok(savedImage);
    }

    // 사용자별 음식 이미지 조회 API
    @GetMapping("/user-images")
    public ResponseEntity<List<FoodImageDTO>> getFoodImagesByUser(@RequestParam String email) {
        List<FoodImageDTO> images = foodImageService.getFoodImagesByUser(email);
        return ResponseEntity.ok(images);
    }

    // 음식 이미지 삭제 API
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFoodImage(@PathVariable Long id) {
        foodImageService.deleteFoodImage(id);
        return ResponseEntity.noContent().build();
    }
}
