CREATE TABLE IF NOT EXISTS `member`
(
    `member_id`                    BIGINT                                   NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '회원 고유 식별자 (PK)',
    `subject`                      VARCHAR(50)                              NOT NULL COMMENT '회원 고유 식별자 (subject)',
    `nickname`                     VARCHAR(20)                              NULL COMMENT '회원 닉네임',
    `provider`                     ENUM ('GOOGLE', 'KAKAO','NAVER','APPLE') NOT NULL COMMENT 'OAuth2 인가 서비스 제공자 코드 (구글: 1, 카카오: 2, 네이버: 3, 애플: 4)',
    `birthday`                     DATE                                     NULL COMMENT '회원 생년월일',
    `gender`                       ENUM ('MALE','FEMALE')                   NULL COMMENT '회원 성별',
    `refresh_token`                VARCHAR(300)                             NULL COMMENT '유저 리프레시 토큰',
    `is_push_notification_enabled` BOOLEAN                                  NOT NULL DEFAULT 0 COMMENT '유저 푸시 알림 설정 여부',
    `is_active`                    BOOLEAN                                  NOT NULL DEFAULT 1 COMMENT '회원 활성화 여부',
    `created_at`                   DATETIME                                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`                   DATETIME                                 NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    UNIQUE INDEX `ux-member-subject` (subject),
    UNIQUE INDEX `ux-member-nickname` (nickname)
);

CREATE TABLE IF NOT EXISTS `meeting`
(
    `meeting_id`          BIGINT                                              NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '모임 고유 식별자',
    `title`               VARCHAR(30)                                         NOT NULL COMMENT '모임 이름(제목)',
    `location`            VARCHAR(200)                                        NOT NULL COMMENT '모임 장소',
    `started_at`          DATETIME                                            NOT NULL COMMENT '모임 시각',
    `kakao_chat_url`      CHAR(33)                                            NOT NULL COMMENT '카카오톡 오픈채팅 URL',
    `kakao_chat_password` VARCHAR(20)                                         NULL COMMENT '카카오톡 오픈채팅 비밀번호',
    `description`         VARCHAR(2000)                                       NULL COMMENT '모임 상세 설명',
    `allowed_gender`      ENUM ('MALE', 'FEMALE')                             NULL COMMENT '모임에 참여할 성별',
    `min_age`             INTEGER                                             NOT NULL COMMENT '모임 참여 최소 연령',
    `max_age`             INTEGER                                             NOT NULL COMMENT '모임 참여 최대 연령',
    `conversation_type`   ENUM ('QUIET', 'LIGHT_CONVERSATION', 'COMFORTABLE') NOT NULL COMMENT '대화할 수 있는 정도',
    `max_participants`    INTEGER                                             NOT NULL COMMENT '모임 참여 최대 인원',
    `is_active`           BOOLEAN                                             NULL DEFAULT 1 COMMENT '모임 활성화 여부',
    `created_at`          DATETIME                                            NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`          DATETIME                                            NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
);

CREATE TABLE IF NOT EXISTS `participant`
(
    `participant_id` BIGINT   NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '모임 참여자 고유 식별자',
    `member_id`      BIGINT   NOT NULL COMMENT '회원 고유 식별자',
    `meeting_id`     BIGINT   NOT NULL COMMENT '모임 고유 식별자',
    `is_master`      BOOLEAN  NOT NULL DEFAULT 0 COMMENT '모임 마스터 권한 여부',
    `is_active`      BOOLEAN  NULL     DEFAULT 1 COMMENT '모임 또는 모임에 참여한 유저 활성화 여부',
    `created_at`     DATETIME NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`     DATETIME NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    UNIQUE INDEX `ux-participant-member_id-meeting_id` (`member_id`, `meeting_id`),
    INDEX `ix-participant-meeting_id` (`meeting_id`)
);
