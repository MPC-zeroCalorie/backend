package com.example.zeroCalorie.service;

import com.example.zeroCalorie.dto.FoodImageDTO;
import com.example.zeroCalorie.entity.FoodImage;
import com.example.zeroCalorie.entity.User;
import com.example.zeroCalorie.repository.FoodImageRepository;
import com.example.zeroCalorie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FoodImageService {

    private final FoodImageRepository foodImageRepository; // 필드명 수정
    private final UserRepository userRepository;

//    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    public FoodImageService(FoodImageRepository foodImageRepository, UserRepository userRepository) {
        this.foodImageRepository = foodImageRepository; // 생성자를 통해 주입
        this.userRepository = userRepository;
    }

    // 음식 이미지 저장
    public FoodImageDTO saveFoodImage(String email, FoodImageDTO foodImageDTO) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        User user = userOpt.get();

        // FoodImage 객체 생성 및 속성 설정
        FoodImage foodImage = new FoodImage();
        foodImage.setUser(user); // 사용자 설정
        foodImage.setImageUrl(foodImageDTO.getImageUrl()); // 이미지 URL 설정
        foodImage.setUrl(foodImageDTO.getUrl()); // 다른 필요한 속성 설정 (예: URL, UUID 등)
        foodImage.setUuid(String.valueOf(UUID.randomUUID())); // UUID 자동 생성 (필요한 경우)

        // 데이터베이스에 저장
        FoodImage savedImage = foodImageRepository.save(foodImage);
        return new FoodImageDTO(savedImage.getUserId(), savedImage.getImageUrl(), savedImage.getUrl(), savedImage.getUuid());
    }

    // 사용자별 음식 이미지 조회
    public List<FoodImageDTO> getFoodImagesByUser(String email) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<FoodImage> images = foodImageRepository.findByUser(user);
        return images.stream()
                .map(image -> new FoodImageDTO(image.getUserId(), image.getImageUrl(), image.getUrl(), image.getUuid()))
                .collect(Collectors.toList());
    }

    // 음식 이미지 삭제
    public void deleteFoodImage(Long id) {
        FoodImage foodImage = foodImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("음식 이미지를 찾을 수 없습니다."));
        foodImageRepository.delete(foodImage);
    }
}
