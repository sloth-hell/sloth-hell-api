CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`                   VARCHAR(50)                              NOT NULL PRIMARY KEY COMMENT '회원 고유 식별자',
    `email`                     VARCHAR(100)                             NOT NULL COMMENT '회원 이메일',
    `profile_url`               VARCHAR(200)                             NOT NULL COMMENT '회원 프로필 사진 URL',
    `nickname`                  VARCHAR(20)                              NULL COMMENT '회원 닉네임',
    `provider`                  ENUM ('GOOGLE', 'KAKAO','NAVER','APPLE') NOT NULL COMMENT 'OAuth2 인가 서비스 제공자 코드 (구글: 1, 카카오: 2, 네이버: 3, 애플: 4)',
    `birthday`                  DATE                                     NULL COMMENT '회원 생년월일',
    `gender`                    ENUM ('MALE','FEMALE')                   NULL COMMENT '회원 성별',
    `created_at`                DATETIME                                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`                DATETIME                                 NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    `activated`                 BOOLEAN                                  NOT NULL DEFAULT 1 COMMENT '회원 활성화 여부',
    `push_notification_enabled` BOOLEAN                                  NOT NULL DEFAULT 0 COMMENT '유저 푸시 알림 설정 여부',
    UNIQUE INDEX `ux-user-email` (email),
    UNIQUE INDEX `ux-user-nickname` (nickname)
);

CREATE TABLE IF NOT EXISTS `refresh_token`
(
    `token`   VARCHAR(300) NOT NULL PRIMARY KEY COMMENT 'user refresh token',
    `user_id` VARCHAR(50)  NOT NULL COMMENT 'OAuth2 Provider name (회원 고유 식별자)'
);
