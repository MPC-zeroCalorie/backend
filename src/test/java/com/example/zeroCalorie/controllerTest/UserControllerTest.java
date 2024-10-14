package com.example.zeroCalorie.controllerTest;

import com.example.zeroCalorie.controller.UserController;
import com.example.zeroCalorie.dto.UserDTO;
import com.example.zeroCalorie.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Mockito 객체 초기화
        MockitoAnnotations.openMocks(this);

        userDTO = UserDTO.builder()
                .userEmail("test@example.com")
                .userNickname("TestUser")
                .build();
    }

    @Test
    void register_shouldReturnCreatedStatus() {
        UserDTO userDTO = UserDTO.builder()
                .userEmail("test@example.com")
                .userNickname("TestUser")
                .build();

        when(userService.register(anyString(), anyString(), anyString())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.register("test@example.com", "TestUser", "password");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userDTO);
    }

    @Test
    void login_shouldReturnOkStatus() {
        UserDTO userDTO = UserDTO.builder()
                .userEmail("test@example.com")
                .userNickname("TestUser")
                .build();

        when(userService.login(anyString(), anyString())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.login("test@example.com", "password");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userDTO);
    }
}
