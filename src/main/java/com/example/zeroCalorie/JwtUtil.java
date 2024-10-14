package com.example.zeroCalorie;

import com.example.zeroCalorie.dto.UserDTO;
import com.example.zeroCalorie.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 비밀 키 설정 (보안을 위해 환경 변수 또는 설정 파일로 관리 추천)
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24시간

    // **JWT 토큰 생성 메서드**
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserEmail())
                .claim("nickname", user.getNickname()) // 커스텀 클레임 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // **JWT 토큰에서 사용자 정보 추출 메서드**
    public UserDTO extractUserFromToken(String token) {
        Claims claims = parseToken(token);
        String email = claims.getSubject();
        String nickname = claims.get("nickname", String.class);

        return UserDTO.builder()
                .userEmail(email)
                .userNickname(nickname)
                .build();
    }

    // **토큰 유효성 검증 메서드**
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // **토큰 파싱 메서드** (클레임 추출)
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
