package com.example.zeroCalorie.dto;

import com.example.zeroCalorie.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
public class UserDTO {
    private Long Id;
    private String userEmail;
    private String userNickname;
    private String password;
    private String token; // JWT 토큰 필드 추가

    @Builder
    public UserDTO(String userEmail, String userNickname) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
    }

    public static UserDTO of(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserEmail(user.getUserEmail());
        userDTO.setUserNickname(user.getNickname());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }
}
