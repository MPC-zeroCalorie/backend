package com.example.zeroCalorie.controllerTest;

import com.example.zeroCalorie.controller.FoodController;
import com.example.zeroCalorie.entity.Food;
import com.example.zeroCalorie.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FoodControllerTest {
    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private Food food;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        food = Food.builder()
                .foodName("Test Food")
                .calories(100)
                .build();
    }

    @Test
    void addFood_shouldReturnCreatedStatus() {
        when(foodService.saveFood(any(Food.class))).thenReturn(food);

        ResponseEntity<Food> response = foodController.addFood(food);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(food);
    }
}
