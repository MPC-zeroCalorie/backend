package com.example.zeroCalorie.serviceTest;

import com.example.zeroCalorie.dto.FoodImageDTO;
import com.example.zeroCalorie.entity.FoodImage;
import com.example.zeroCalorie.entity.User;
import com.example.zeroCalorie.repository.FoodImageRepository;
import com.example.zeroCalorie.repository.UserRepository;
import com.example.zeroCalorie.service.FoodImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FoodImageServiceTest {

    @InjectMocks
    private FoodImageService foodImageService;

    @Mock
    private FoodImageRepository foodImageRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private FoodImage foodImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        foodImage = FoodImage.builder()
                .imageUrl("http://test.com/image.jpg")
                .path("test/path")
                .url("http://test.com/image.jpg")
                .user(user) // 저장된 User 사용
                .uuid(UUID.randomUUID().toString())
                .build();
        foodImageRepository.save(foodImage); // FoodImage 저장

        user = User.builder().userEmail("test@example.com").build();
        foodImage = FoodImage.builder().id(1L).imageUrl("http://example.com/image.jpg").user(user).build();
    }

    @Test
    void saveFoodImage() {
        when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(Optional.of(user));
        when(foodImageRepository.save(any(FoodImage.class))).thenReturn(foodImage);

        FoodImageDTO foodImageDTO = new FoodImageDTO();
        foodImageDTO.setImageUrl("http://example.com/image.jpg");

        FoodImageDTO savedImage = foodImageService.saveFoodImage(user.getUserEmail(), foodImageDTO);
        assertNotNull(savedImage);
        assertEquals(foodImage.getImageUrl(), savedImage.getImageUrl());
    }

    @Test
    void getFoodImagesByUser() {
        when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(Optional.of(user));
        when(foodImageRepository.findByUser(user)).thenReturn(List.of(foodImage));

        List<FoodImageDTO> images = foodImageService.getFoodImagesByUser(user.getUserEmail());
        assertFalse(images.isEmpty());
        assertEquals(1, images.size());
        assertEquals(foodImage.getImageUrl(), images.get(0).getImageUrl());
    }

    @Test
    void deleteFoodImage() {
        when(foodImageRepository.findById(1L)).thenReturn(Optional.of(foodImage));
        doNothing().when(foodImageRepository).delete(foodImage);

        assertDoesNotThrow(() -> foodImageService.deleteFoodImage(1L));
        verify(foodImageRepository, times(1)).delete(foodImage);
    }
}
