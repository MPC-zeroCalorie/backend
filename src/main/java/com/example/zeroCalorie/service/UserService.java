package com.example.zeroCalorie.service;

import com.example.zeroCalorie.JwtUtil;
import com.example.zeroCalorie.dto.UserDTO;
import com.example.zeroCalorie.entity.User;
import com.example.zeroCalorie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public UserDTO login(String email, String password) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user); // JWT 생성
            return new UserDTO(user.getId(), user.getUserEmail(), user.getNickname(), user.getPassword(), token);
        }
        return null;
    }

    public UserDTO register(String email, String nickname, String password) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            return null;
        } else {
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(password);
            User newUser = User.builder()
                    .userEmail(email)
                    .nickname(nickname)
                    .password(encodedPassword) // 암호화된 비밀번호 저장
                    .build();
            User savedUser = userRepository.save(newUser);
            return new UserDTO(savedUser.getId(), savedUser.getUserEmail(), savedUser.getNickname(), savedUser.getPassword(), null); // 토큰은 초기화
        }
    }
}
