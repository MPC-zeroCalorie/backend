package com.example.zeroCalorie.entity;

import com.example.zeroCalorie.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, length = 200)
    private String userEmail;

    @Column(length = 200)
    private String password;

    @Column(nullable = false, length = 200)
    private String nickname;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp updatedAt;

    public static User fromUserDTO(UserDTO userDTO) {
        return User.builder()
                .Id(userDTO.getId())
                .userEmail(userDTO.getUserEmail())
                .nickname(userDTO.getUserNickname())
                .password(userDTO.getPassword())
                .build();
    }
}
