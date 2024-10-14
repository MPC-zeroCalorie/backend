package com.example.zeroCalorie.entity;

import com.example.zeroCalorie.dto.FoodDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "foods")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodName;
    private int calories;
    private double carbs;
    private double protein;
    private double fat;

    @Column(name = "consumption_date", nullable = false) // NULL을 허용하지 않도록 설정
    private LocalDate consumptionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // User와 연관 관계
    private User user;

    // FoodDTO를 Food로 변환하는 메서드
    public static Food fromDTO(FoodDTO foodDTO) {
        Food food = new Food();
        food.setFoodName(foodDTO.getFoodName());
        food.setCalories(foodDTO.getCalories());
        food.setCarbs(foodDTO.getCarbs());
        food.setProtein(foodDTO.getProtein());
        food.setFat(foodDTO.getFat());
        food.setConsumptionDate(foodDTO.getConsumptionDate());
        return food;
    }
}
