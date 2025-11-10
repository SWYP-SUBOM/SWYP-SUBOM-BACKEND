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
