package com.example.zeroCalorie.controllerTest;

import com.example.zeroCalorie.controller.FoodImageController;
import com.example.zeroCalorie.dto.FoodImageDTO;
import com.example.zeroCalorie.service.FoodImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FoodImageControllerTest {

    @InjectMocks
    private FoodImageController foodImageController;

    @Mock
    private FoodImageService foodImageService;

    private FoodImageDTO foodImageDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        foodImageDTO = new FoodImageDTO();
        foodImageDTO.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void saveFoodImage() {
        when(foodImageService.saveFoodImage(any(String.class), any(FoodImageDTO.class))).thenReturn(foodImageDTO);

        ResponseEntity<FoodImageDTO> response = foodImageController.saveFoodImage("test@example.com", foodImageDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(foodImageDTO, response.getBody());
    }

    @Test
    void getFoodImagesByUser() {
        when(foodImageService.getFoodImagesByUser(any(String.class))).thenReturn(List.of(foodImageDTO));

        ResponseEntity<List<FoodImageDTO>> response = foodImageController.getFoodImagesByUser("test@example.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void deleteFoodImage() {
        doNothing().when(foodImageService).deleteFoodImage(1L);

        ResponseEntity<Void> response = foodImageController.deleteFoodImage(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
