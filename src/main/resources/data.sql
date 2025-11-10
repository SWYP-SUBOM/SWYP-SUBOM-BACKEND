-- ReactionType 기본 데이터
INSERT INTO sseobom.reaction_type (reaction_name)
VALUES
    ('LIKE'),
    ('EMPATHY'),
    ('INSIGHTFUL');

-- Category 기본 데이터
INSERT INTO sseobom.category (category_name, created_at, updated_at)
VALUES
    ('일상', NOW(), NOW()),
    ('인간관계', NOW(), NOW()),
    ('문화·트렌드', NOW(), NOW()),
    ('가치관', NOW(), NOW()),
    ('진로·성장', NOW(), NOW());

-- Topic 기본 데이터
-- Category 1 (일상)
INSERT INTO sseobom.topic (topic_name, category_id, is_used, created_at, updated_at)
VALUES
    ('오늘 하루 중 가장 기억에 남는 순간은?', 1, false, NOW(), NOW()),
    ('최근에 웃었던 일은 무엇인가요?', 1, false, NOW(), NOW()),
    ('하루를 마무리할 때 어떤 생각을 하나요?', 1, false, NOW(), NOW());

-- Category 2 (인간관계)
INSERT INTO sseobom.topic (topic_name, category_id, is_used, created_at, updated_at)
VALUES
    ('친구 관계에서 가장 중요하다고 생각하는 것은?', 2, false, NOW(), NOW()),
    ('최근 누군가에게 고마움을 느낀 적이 있나요?', 2, false, NOW(), NOW());

-- Category 3 (문화·트렌드)
INSERT INTO sseobom.topic (topic_name, category_id, is_used, created_at, updated_at)
VALUES
    ('요즘 가장 흥미롭게 본 콘텐츠는?', 3, false, NOW(), NOW()),
    ('문화생활을 통해 얻는 영감은 어떤가요?', 3, false, NOW(), NOW());

-- Category 4 (가치관)
INSERT INTO sseobom.topic (topic_name, category_id, is_used, created_at, updated_at)
VALUES
    ('인생에서 가장 중요한 가치는 무엇인가요?', 4, false, NOW(), NOW()),
    ('당신의 신념은 어떤 상황에서도 흔들리지 않나요?', 4, false, NOW(), NOW());

-- Category 5 (취미, 취향)
INSERT INTO sseobom.topic (topic_name, category_id, is_used, created_at, updated_at)
VALUES
    ('당신이 가장 좋아하는 영화 장르와, 잘 맞지 않는 장르가 있다면 각각 무엇인가요? ', 5, false, NOW(), NOW()),
    ('아날로그 방식의 기록 (손글씨, 다이어리 등)과 디지털 방식의 기록 중, 어떤 것이 당신의 기억력과 사고력에 더 도움을 준다고 생각하시나요?', 5, false, NOW(), NOW());

-----------------------------
-- User 기본 데이터 (엔티티: user)
INSERT INTO sseobom.users (user_id, email, kakao_id, role, created_at, user_name, is_deleted)
VALUES
    (1, 'user1@example.com', 'kakao_id_1', 'ROLE_USER', NOW(), '테스터1', false),
    (2, 'user2@example.com', 'kakao_id_2', 'ROLE_USER', NOW(), '유저2', false);

-- Post 기본 데이터 (엔티티: post)
-- (25개: user_id = 1)
INSERT INTO sseobom.post (post_id, topic_id, user_id, content, status, is_revised, created_at, updated_at, nickname)
VALUES
    (1, 1, 1, '오늘의 일기 본문 1', 'PUBLISHED', false, NOW() - INTERVAL '25' DAY, NOW() - INTERVAL '25' DAY, '테스터1_닉네임1'),
    (2, 2, 1, '오늘의 일기 본문 2', 'PUBLISHED', false, NOW() - INTERVAL '24' DAY, NOW() - INTERVAL '24' DAY, '테스터1_닉네임2'),
    (3, 3, 1, '오늘의 일기 본문 3', 'PUBLISHED', true, NOW() - INTERVAL '23' DAY, NOW() - INTERVAL '23' DAY, '테스터1_닉네임3'),
    (4, 4, 1, '인간관계 글 본문 4', 'PUBLISHED', false, NOW() - INTERVAL '22' DAY, NOW() - INTERVAL '22' DAY, '테스터1_닉네임4'),
    (5, 5, 1, '인간관계 글 본문 5', 'PUBLISHED', false, NOW() - INTERVAL '21' DAY, NOW() - INTERVAL '21' DAY, '테스터1_닉네임5'),
    (6, 6, 1, '문화·트렌드 글 본문 6', 'PUBLISHED', false, NOW() - INTERVAL '20' DAY, NOW() - INTERVAL '20' DAY, '테스터1_닉네임6'),
    (7, 7, 1, '문화·트렌드 글 본문 7', 'PUBLISHED', false, NOW() - INTERVAL '19' DAY, NOW() - INTERVAL '19' DAY, '테스터1_닉네임7'),
    (8, 8, 1, '가치관 글 본문 8', 'PUBLISHED', true, NOW() - INTERVAL '18' DAY, NOW() - INTERVAL '18' DAY, '테스터1_닉네임8'),
    (9, 9, 1, '가치관 글 본문 9', 'PUBLISHED', false, NOW() - INTERVAL '17' DAY, NOW() - INTERVAL '17' DAY, '테스터1_닉네임9'),
    (10, 10, 1, '진로·성장 글 본문 10', 'PUBLISHED', false, NOW() - INTERVAL '16' DAY, NOW() - INTERVAL '16' DAY, '테스터1_닉네임10'),
    (11, 11, 1, '진로·성장 글 본문 11', 'PUBLISHED', false, NOW() - INTERVAL '15' DAY, NOW() - INTERVAL '15' DAY, '테스터1_닉네임11'),
    (12, 1, 1, '오늘의 일기 본문 12', 'PUBLISHED', false, NOW() - INTERVAL '14' DAY, NOW() - INTERVAL '14' DAY, '테스터1_닉네임12'),
    (13, 2, 1, '오늘의 일기 본문 13', 'PUBLISHED', false, NOW() - INTERVAL '13' DAY, NOW() - INTERVAL '13' DAY, '테스터1_닉네임13'),
    (14, 3, 1, '오늘의 일기 본문 14', 'PUBLISHED', true, NOW() - INTERVAL '12' DAY, NOW() - INTERVAL '12' DAY, '테스터1_닉네임14'),
    (15, 4, 1, '인간관계 글 본문 15', 'PUBLISHED', false, NOW() - INTERVAL '11' DAY, NOW() - INTERVAL '11' DAY, '테스터1_닉네임15'),
    (16, 5, 1, '인간관계 글 본문 16', 'PUBLISHED', false, NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '10' DAY, '테스터1_닉네임16'),
    (17, 6, 1, '문화·트렌드 글 본문 17', 'PUBLISHED', false, NOW() - INTERVAL '9' DAY, NOW() - INTERVAL '9' DAY, '테스터1_닉네임17'),
    (18, 7, 1, '문화·트렌드 글 본문 18', 'PUBLISHED', false, NOW() - INTERVAL '8' DAY, NOW() - INTERVAL '8' DAY, '테스터1_닉네임18'),
    (19, 8, 1, '가치관 글 본문 19', 'PUBLISHED', true, NOW() - INTERVAL '7' DAY, NOW() - INTERVAL '7' DAY, '테스터1_닉네임19'),
    (20, 9, 1, '가치관 글 본문 20', 'PUBLISHED', false, NOW() - INTERVAL '6' DAY, NOW() - INTERVAL '6' DAY, '테스터1_닉네임20'),
    (21, 10, 1, '진로·성장 글 본문 21', 'PUBLISHED', false, NOW() - INTERVAL '5' DAY, NOW() - INTERVAL '5' DAY, '테스터1_닉네임21'),
    (22, 11, 1, '진로·성장 글 본문 22', 'PUBLISHED', false, NOW() - INTERVAL '4' DAY, NOW() - INTERVAL '4' DAY, '테스터1_닉네임22'),
    (23, 1, 1, '오늘의 일기 본문 23', 'PUBLISHED', false, NOW() - INTERVAL '3' DAY, NOW() - INTERVAL '3' DAY, '테스터1_닉네임23'),
    (24, 2, 1, '오늘의 일기 본문 24', 'PUBLISHED', false, NOW() - INTERVAL '2' DAY, NOW() - INTERVAL '2' DAY, '테스터1_닉네임24'),
    (25, 3, 1, '오늘의 일기 본문 25 (가장 최신 글)', 'PUBLISHED', true, NOW() - INTERVAL '1' DAY, NOW() - INTERVAL '1' DAY, '테스터1_닉네임25');

-- (5개: user_id = 2)
INSERT INTO sseobom.post (post_id, topic_id, user_id, content, status, is_revised, created_at, updated_at, nickname)
VALUES
    (26, 1, 2, '유저2의 일상 글', 'PUBLISHED', false, NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '10' DAY, '유저2_닉네임1'),
    (27, 4, 2, '유저2의 인간관계 글', 'PUBLISHED', false, NOW() - INTERVAL '8' DAY, NOW() - INTERVAL '8' DAY, '유저2_닉네임2'),
    (28, 8, 2, '유저2의 가치관 글', 'PUBLISHED', false, NOW() - INTERVAL '6' DAY, NOW() - INTERVAL '6' DAY, '유저2_닉네임3'),
    (29, 10, 2, '유저2의 진로 성장 글', 'PUBLISHED', false, NOW() - INTERVAL '4' DAY, NOW() - INTERVAL '4' DAY, '유저2_닉네임4'),
    (30, 2, 2, '유저2의 최근 일상 글', 'PUBLISHED', false, NOW() - INTERVAL '2' DAY, NOW() - INTERVAL '2' DAY, '유저2_닉네임5');


-- Reaction 기본 데이터 (엔티티: feed_reaction 테이블, feed_reaction_id PK)
INSERT INTO sseobom.feed_reaction (feed_reaction_id, user_id, post_id, reaction_type_id, created_at, updated_at)
VALUES
    (1, 1, 26, 1, NOW(), NOW()), -- '테스터1'이 '유저2'의 26번 글에 'LIKE'
    (2, 1, 27, 2, NOW(), NOW()), -- '테스터1'이 '유저2'의 27번 글에 'EMPATHY'
    (3, 2, 1, 1, NOW(), NOW()),  -- '유저2'가 '테스터1'의 1번 글에 'LIKE'
    (4, 2, 25, 3, NOW(), NOW()), -- '유저2'가 '테스터1'의 25번 글에 'INSIGHTFUL'
    (5, 1, 10, 1, NOW(), NOW()), -- '테스터1'이 자신의 10번 글에 'LIKE'
    (6, 2, 10, 2, NOW(), NOW()); -- '유저2'가 '테스터1'의 10번 글에 'EMPATHY'