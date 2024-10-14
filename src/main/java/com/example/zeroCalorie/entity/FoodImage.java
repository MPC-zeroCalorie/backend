package com.example.zeroCalorie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_images")
public class FoodImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 또는 User 객체로 연결될 수 있음

    @Getter
    @Column(name = "image_url", nullable = false)
    private String imageUrl; // 이미지 URL 필드

    @Column(nullable = false)
    private String path;  // 서버 내부 경로

    @Column(nullable = false)
    private String url;   // 외부 접근용 URL

    @Column(nullable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();  // UUID 기본값 설정

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false) // 중복 매핑을 방지하기 위해 추가)
    private User user;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
