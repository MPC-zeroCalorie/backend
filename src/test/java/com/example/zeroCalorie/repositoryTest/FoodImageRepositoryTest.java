package com.example.zeroCalorie.repositoryTest;

import com.example.zeroCalorie.entity.FoodImage;
import com.example.zeroCalorie.repository.FoodImageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")  // 테스트 프로파일을 활성화
//@ContextConfiguration(classes = {FoodImageRepository.class})
@Transactional  // 테스트 후 데이터 롤백
public class FoodImageRepositoryTest {
    @Autowired
    private FoodImageRepository foodImageRepository;

    private FoodImage foodImage;

    @BeforeEach
    void setUp() {
        foodImage = FoodImage.builder()
                .imageUrl("http://test.com/image.jpg")
                .path("test/path")
                .url("http://test.com/image.jpg")
                .userId(1L) // 적절한 user_id 값으로 설정
                .uuid(UUID.randomUUID().toString()) // UUID 값 설정
                .build();
        foodImageRepository.save(foodImage);
    }

    @Test
    void findByPath_shouldReturnFoodImage() {
        Optional<FoodImage> foundImage = foodImageRepository.findByPath("test/path");
        assertThat(foundImage).isPresent(); // 이미지가 존재해야 함
        assertThat(foundImage.get().getPath()).isEqualTo("test/path"); // 경로 검증
        assertThat(foundImage.get().getUrl()).isEqualTo("http://test.com/image.jpg"); // URL 검증
    }

    @Test
    void findByPath_shouldReturnEmpty_whenImageNotFound() {
        Optional<FoodImage> foundImage = foodImageRepository.findByPath("nonexistent/path");
        assertThat(foundImage).isNotPresent(); // 이미지가 존재하지 않아야 함
    }

    @Test
    void save_shouldPersistFoodImage() {
        FoodImage newImage = FoodImage.builder()
                .imageUrl("http://test.com/image.jpg")
                .path("new/test/path")
                .url("http://test.com/new_image.jpg")
                .build();
        FoodImage savedImage = foodImageRepository.save(newImage);
        assertThat(savedImage.getId()).isNotNull(); // ID가 생성되어야 함
        assertThat(savedImage.getPath()).isEqualTo("new/test/path"); // 경로 검증
        assertThat(savedImage.getUrl()).isEqualTo("http://test.com/new_image.jpg"); // URL 검증
    }

    @Test
    void delete_shouldRemoveFoodImage() {
        foodImageRepository.delete(foodImage);
        Optional<FoodImage> foundImage = foodImageRepository.findByPath("test/path");
        assertThat(foundImage).isNotPresent(); // 이미지가 삭제되어야 함
    }
}
