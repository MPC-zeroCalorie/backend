package com.example.zeroCalorie.repository;

import com.example.zeroCalorie.entity.FoodImage;
import com.example.zeroCalorie.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodImageRepository extends JpaRepository<FoodImage, Long> {
    List<FoodImage> findByUser(User user);
    Optional<FoodImage> findByPath(String path);
}
