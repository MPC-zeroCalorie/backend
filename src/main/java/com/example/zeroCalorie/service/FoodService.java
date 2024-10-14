package com.example.zeroCalorie.service;

import com.example.zeroCalorie.dto.FoodDTO;
import com.example.zeroCalorie.entity.Food;
import com.example.zeroCalorie.entity.User;
import com.example.zeroCalorie.repository.FoodRepository;
import com.example.zeroCalorie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService<LocalDate> {

    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    // 음식 데이터 저장
    public FoodDTO saveFood(String email, FoodDTO foodDTO) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        User user = userOpt.get();

        Food food = Food.builder()
                .foodName(foodDTO.getFoodName())
                .calories(foodDTO.getCalories())
                .carbs(foodDTO.getCarbs())
                .protein(foodDTO.getProtein())
                .fat(foodDTO.getFat())
                .consumptionDate(foodDTO.getConsumptionDate())
                .user(user)
                .build();

        Food savedFood = foodRepository.save(food);
        return FoodDTO.of(savedFood);
    }

    // 직접 Food 객체를 저장하는 메서드 추가
    public Food saveFood(Food food) {
        return foodRepository.save(food);
    }

    // 사용자별 일간 음식 기록 조회
    public List<FoodDTO> getDailyRecords(String email, LocalDate date) {
        User user = findUserByEmail(email);
        List<Food> foods = foodRepository.findByUserAndConsumptionDate(user, (java.time.LocalDate) date);
        return convertToDTOList(foods);
    }

    // 사용자별 주간 음식 기록 조회
    public List<FoodDTO> getWeeklyRecords(String email, int year, int weekOfYear) {
        User user = findUserByEmail(email);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        List<Food> foods = foodRepository.findByUser(user).stream()
                .filter(food -> food.getConsumptionDate().getYear() == year &&
                        food.getConsumptionDate().get(weekFields.weekOfWeekBasedYear()) == weekOfYear)
                .collect(Collectors.toList());

        return convertToDTOList(foods);
    }

    // 사용자별 월간 음식 기록 조회
    public List<FoodDTO> getMonthlyRecords(String email, int year, int month) {
        User user = findUserByEmail(email);

        List<Food> foods = foodRepository.findByUser(user).stream()
                .filter(food -> food.getConsumptionDate().getYear() == year &&
                        food.getConsumptionDate().getMonthValue() == month)
                .collect(Collectors.toList());

        return convertToDTOList(foods);
    }

    // 음식 데이터 삭제
    public void deleteFood(Long foodId) {
        foodRepository.deleteById(foodId);
    }

    // 이메일로 사용자 조회
    private User findUserByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // Food 엔티티 목록을 DTO 목록으로 변환
    private List<FoodDTO> convertToDTOList(List<Food> foods) {
        return foods.stream()
                .map(FoodDTO::of)
                .collect(Collectors.toList());
    }

    // 음식 이름으로 FoodDTO 찾기
    public Optional<FoodDTO> findFoodByName(String foodName) {
        Optional<Food> foodOpt = foodRepository.findByFoodName(foodName);
        return foodOpt.map(FoodDTO::of);
    }
}
