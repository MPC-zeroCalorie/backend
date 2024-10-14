# zeroCalorie database schema

CREATE DATABASE zeroCalorie;
USE zeroCalorie;

-- drop table users;
-- drop table foods;
-- drop table food_images;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_email VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(200),
    nickname VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 음식 (기록) 테이블
CREATE TABLE IF NOT EXISTS foods (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    food_name VARCHAR(200) NOT NULL, -- 음식 이름
    calories INT NOT NULL, -- 칼로리
    carbs DOUBLE NOT NULL, -- 탄수화물
    protein DOUBLE NOT NULL, -- 단백질
    fat DOUBLE NOT NULL, -- 지방
    consumption_date DATE NOT NULL, -- 소비 날짜
    user_id BIGINT UNSIGNED, -- 사용자와 연결
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 음식 이미지 테이블
CREATE TABLE IF NOT EXISTS food_images (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    food_id BIGINT UNSIGNED NOT NULL, -- 음식과 연결
    image_url VARCHAR(255) NOT NULL, -- 이미지 URL
    path VARCHAR(500) NOT NULL, -- 서버 내부 경로
    url VARCHAR(500) NOT NULL, -- 이미지 접근용 URL
    uuid VARCHAR(100) NOT NULL UNIQUE, -- 고유 식별자
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
);
