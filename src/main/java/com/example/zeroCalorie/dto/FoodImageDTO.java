package com.example.zeroCalorie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
public class FoodImageDTO {
    private Long id;           // 이미지 ID
    private String imageUrl;   // 이미지 URL
    private String path;       // 서버 내부 경로
    private String url;        // 이미지 접근용 URL
    private String uuid;       // 고유 식별자

    // 추가: 모든 필드를 초기화하는 생성자
    public FoodImageDTO(Long id, String imageUrl, String url, String uuid) {
        this.id = id;
        this.imageUrl = imageUrl;
//        this.path = path;
        this.url = url;
        this.uuid = uuid;
    }
}
