package com.example.zeroCalorie.dto;

import com.example.zeroCalorie.entity.Food;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
public class FoodDTO {

    private String foodName;
    private int calories;
    private double carbs;
    private double protein;
    private double fat;
    private LocalDate consumptionDate;

    public static FoodDTO of(Food food) {
        return FoodDTO.builder()
                .foodName(food.getFoodName())
                .calories(food.getCalories())
                .carbs(food.getCarbs())
                .protein(food.getProtein())
                .fat(food.getFat())
                .consumptionDate(food.getConsumptionDate())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodDTO foodDTO)) return false;
        return calories == foodDTO.calories &&
                Double.compare(foodDTO.carbs, carbs) == 0 &&
                Double.compare(foodDTO.protein, protein) == 0 &&
                Double.compare(foodDTO.fat, fat) == 0 &&
                Objects.equals(foodName, foodDTO.foodName) &&
                Objects.equals(consumptionDate, foodDTO.consumptionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodName, calories, carbs, protein, fat, consumptionDate);
    }
}

