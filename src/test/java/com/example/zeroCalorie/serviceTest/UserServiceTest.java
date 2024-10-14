package com.example.zeroCalorie.serviceTest;

import com.example.zeroCalorie.JwtUtil;
import com.example.zeroCalorie.dto.UserDTO;
import com.example.zeroCalorie.entity.User;
import com.example.zeroCalorie.repository.UserRepository;
import com.example.zeroCalorie.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock // JwtUtil Mock 추가
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User user;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder(); // BCryptPasswordEncoder 인스턴스 초기화

        user = User.builder()
                .Id(1L)
                .userEmail("test@example.com")
                .password("password")
                .nickname("TestUser")
                .build();
        userRepository.save(user); // User 저장
    }

    @Test
    void register_shouldReturnUserDTO_whenUserDoesNotExist() {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.register("test@example.com", "TestUser", "password");

        assertThat(result).isNotNull();
        assertThat(result.getUserEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void login_shouldReturnUserDTO_whenValidCredentials() {
        // Given
        String email = "test@example.com";
        String password = "validPassword";

        User user = User.builder()
                .Id(1L)
                .userEmail(email)
                .nickname("TestUser")
                .password(passwordEncoder.encode(password)) // 암호화된 비밀번호
                .build();

        // Mockito 설정: 유효한 사용자 반환
        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("generatedToken"); // Mock JWT 생성

        // When
        UserDTO result = userService.login(email, password);

        // Then
        assertNotNull(result); // null이 아닌 결과 확인
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUserEmail(), result.getUserEmail());
        assertEquals(user.getNickname(), result.getUserNickname());
        // 추가적으로 토큰 검사 가능
    }

    @Test
    public void login_shouldReturnNull_whenInvalidCredentials() {
        // Given
        String invalidUsername = "invalidUser";
        String invalidPassword = "invalidPass";

        // When
        UserDTO result = userService.login(invalidUsername, invalidPassword); // UserDTO로 직접 받기

        // Then
        assertNull(result); // null을 기대함
    }
}
