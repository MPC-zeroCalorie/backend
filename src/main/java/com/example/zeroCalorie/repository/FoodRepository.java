package com.example.zeroCalorie.repository;

import com.example.zeroCalorie.entity.Food;
import com.example.zeroCalorie.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByUser(User user);
    List<Food> findByUserAndConsumptionDate(User user, LocalDate date);

    Optional<Food> findByFoodName(String foodName);
}
