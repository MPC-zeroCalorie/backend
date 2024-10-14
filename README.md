# backend

2024.10.14
사용자 회원가입 & 로그인 기능 구현 완료
db 작성 완료 (schema.sql 코드와 동일) ----- 내용 보완/수정 필요
테스트 코드는 아직 해결 안 된 오류 남음



user - 사용자 회원가입 & 로그인 정보 
        (Id, userEmail, userNickname, password, JWT token)
food - 음식 정보 
        (calories, carbs(탄), protein(단), fat(지), consumptionDate(소비 날짜))
foodImage - 음식 이미지 정보 
        (id(이미지 id), imageUrl(이미지 url), path(서버 경로), url(이미지 정보 접근용 url), uuid(이미지 고유 식별))
