package com.example.zeroCalorie.controller;

import com.example.zeroCalorie.dto.UserDTO;
import com.example.zeroCalorie.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam String password) {
        UserDTO user = userService.register(email, nickname, password);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.CREATED); // UserDTO 반환
        }
        return ResponseEntity.badRequest().build(); // Bad request 처리
    }

    // 로그인 API - JWT 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestParam String email,
            @RequestParam String password) {
        try {
            UserDTO user = userService.login(email, password); // UserDTO 반환
            return ResponseEntity.ok(user); // UserDTO를 직접 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build(); // 인증 실패 시 401 응답
        }
    }
}
