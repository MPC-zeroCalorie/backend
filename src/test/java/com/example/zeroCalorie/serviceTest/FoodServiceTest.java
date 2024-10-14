package com.example.zeroCalorie.serviceTest;

import com.example.zeroCalorie.dto.FoodDTO;
import com.example.zeroCalorie.entity.Food;
import com.example.zeroCalorie.repository.FoodRepository;
import com.example.zeroCalorie.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class FoodServiceTest {
    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    private Food food;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        food = Food.builder()
                .id(1L)
                .foodName("Test Food")
                .calories(100)
                .build();
    }

    @Test
    void saveFood_shouldReturnSavedFood() {
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        Food savedFood = foodService.saveFood(food);

        assertThat(savedFood).isNotNull();
        assertThat(savedFood.getFoodName()).isEqualTo("Test Food");
    }

    @Test
    public void findFoodByName_shouldReturnFood() {
        // Given
        FoodDTO foodDTO = FoodDTO.builder()
                .foodName("Apple")
                .calories(95)
                .carbs(25)
                .protein(0.5)
                .fat(0.3)
                .consumptionDate(LocalDate.now())
                .build();

        Food foodEntity = Food.fromDTO(foodDTO); // DTO를 엔티티로 변환

        // Mocking the behavior of the repository
        when(foodRepository.findByFoodName("Apple")).thenReturn(Optional.of(foodEntity));

        // When
        Optional<FoodDTO> actualFoodDTOOpt = foodService.findFoodByName("Apple");

        // Then
        assertTrue(actualFoodDTOOpt.isPresent(), "Expected food DTO to be present"); // Optional이 비어 있지 않도록 확인
        FoodDTO actualFoodDTO = actualFoodDTOOpt.get(); // Optional에서 FoodDTO 가져오기
        assertEquals(foodDTO, actualFoodDTO); // DTO가 동일한지 확인
    }
}
