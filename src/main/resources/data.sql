INSERT IGNORE INTO service.meeting(meeting_id, creator_id, title, location, started_at, kakao_chat_url,
                                   kakao_chat_password,
                                   description,
                                   allowed_gender, min_age, max_age, conversation_type)
VALUES (1, '3234291326', '모각코 4인 모집', '스타벅스 과천DT점', '2024-02-05T09:00:00', 'https://open.kakao.com/o/abcdefgh', NULL,
        '모여서 각자 코딩하실 3분을 더 모집합니다!', NULL, 20, 30, 'LIGHT_CONVERSATION'),
       (2, 'caWRAXPearpjDeM49RkmzY7n5JfoT-SUKwf4a7RH-No', '개발자 면접 스터디', '이디야커피 영등포아크로타워점', '2024-03-01T13:00:00',
        'https://open.kakao.com/o/12abkl39', '1111',
        '개발자 네카라쿠배 면접 스터디', NULL, NULL, NULL, 'COMPORTABLE'),
       (3, '??', '공무원 스터디 스파르타 미라클모닝 스터디원 모집', '할리스 여의도전령련회관점', '2024-02-29T07:00:00',
        'https://open.kakao.com/o/bfw2391s',
        '123123aa',
        '합격이 간절하신 분들, 공부 정말 열심히 하시는 분들 하루종일 같이 해요!', 'FEMALE', NULL, NULL, 'QUIET');

INSERT IGNORE INTO service.user_meeting(user_id, meeting_id)
VALUES ('3234291326', 1),
       ('caWRAXPearpjDeM49RkmzY7n5JfoT-SUKwf4a7RH-No', 1),
       ('caWRAXPearpjDeM49RkmzY7n5JfoT-SUKwf4a7RH-No', 2);

SELECT *
FROM user;

SELECT *
FROM meeting;

SELECT *
FROM user_meeting;

SELECT m.meeting_id,
       m.title,
       m.location,
       m.started_at,
       m.description,
       m.allowed_gender,
       m.min_age,
       m.max_age,
       m.conversation_type,
       u.nickname
FROM meeting m
         INNER JOIN user_meeting um ON m.meeting_id = um.meeting_id
         INNER JOIN user u ON um.user_id = u.user_id
WHERE m.activated = 1
  AND u.activated = 1
  AND um.is_master = 1
  AND m.started_at > NOW()
LIMIT 0, 10;
