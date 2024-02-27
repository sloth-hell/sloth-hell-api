# 나태지옥

## ERD

```mermaid
---
title: 나태지옥 ERD
---
erDiagram
    MEMBER ||--o{ PARTICIPANT : member_id
    MEMBER {
        member_id long
        subject string
        email string
        profile_url string
        nickname string
        provider enum
        birthday date
        gender enum
        refresh_token string
        is_push_notification_enabled boolean
        is_active boolean
        created_at datetime
        updated_at datetime
    }
    MEETING ||--o{ PARTICIPANT : meeting_id
    MEETING {
        meeting_id BIGINT
        title string
        location string
        started_at datetime
        kakao_chat_url string
        kakao_chat_password string
        description string
        allowed_gender enum
        min_age tinyint
        max_age tinyint
        conversation_type enum
        is_active boolean
        created_at datetime
        updated_at datetime
    }
    PARTICIPANT {
        participant_id BIGINT
        member_id BIGINT
        meeting_id BIGINT
        is_master boolean
        is_active boolean
        created_at datetime
        updated_at datetime
    }
```
