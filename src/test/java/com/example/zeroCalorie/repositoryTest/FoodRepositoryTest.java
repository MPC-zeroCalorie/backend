package com.example.zeroCalorie.repositoryTest;

import com.example.zeroCalorie.entity.Food;
import com.example.zeroCalorie.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/test_schema.sql") // SQL 초기화 파일 경로
public class FoodRepositoryTest {
    @Autowired
    private FoodRepository foodRepository;

    private Food food;

    @BeforeEach
    void setUp() {
        food = Food.builder()
                .foodName("Test Food")
                .calories(100)
                .build();
        foodRepository.save(food);
    }

    @Test
    void findByFoodName_shouldReturnFood() {
        Optional<Food> foundFood = foodRepository.findByFoodName("Test Food");
        assertThat(foundFood).isPresent();
        assertThat(foundFood.get().getCalories()).isEqualTo(100);
    }
}
