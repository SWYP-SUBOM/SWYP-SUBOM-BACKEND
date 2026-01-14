package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.TodayPostResponse;
import swyp_11.ssubom.domain.post.service.PostService;
import swyp_11.ssubom.domain.topic.dto.*;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.Status;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicType;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;
import swyp_11.ssubom.domain.user.dto.StreakResponse;
import swyp_11.ssubom.domain.user.service.UserService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TopicService {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final PostService postService;
    private final TopicAIService topicAIService;

    @Transactional
    public Optional<Topic> ensureTodayPicked(Long categoryId) {
        LocalDate today = LocalDate.now(KST);

        //0. 이미 오늘 할당이 완료된 경우
        Optional<Topic> existing = topicRepository.findByCategory_IdAndIsUsedTrueAndUsedAt(categoryId, today);
        if(existing.isPresent()) {
            return existing;
        }
        //1순위
        Optional<Topic> reserved = topicRepository.findReservedTopic(categoryId,today);
            Topic targetTopic;
            if(reserved.isPresent()){
                targetTopic =reserved.get();
                if(!targetTopic.isUsed()){
                    targetTopic.use(today);
                } //isUsed = ture처리
                return Optional.of(targetTopic);
            }

        // 2순위
        Topic topic =topicRepository.lockOneUnused(categoryId);

        //할당할 수 있는 주제가 없음
        if (topic == null) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_TOPIC);
        }
        topic.use(today);
        return Optional.of(topic);
    }

    @Transactional
    public Optional<TodayTopicResponseDto> ensureTodayPickedDto(Long categoryId) {
        return ensureTodayPicked(categoryId).map(t ->
                TodayTopicResponseDto.of(
                        t.getCategory().getName(),
                        t.getName(),
                        t.getCategory().getId(),
                        t.getId(),
                        t.getTopicType()
                )
        );
    }

    public TopicListResponse getAll(Long categoryId, String sort) {
        List<Topic> topics;
        if(sort.equals("latest")){
             topics = topicRepository.findTop30ByCategoryIdAndIsUsedTrueOrderByUsedAtDesc(categoryId);
        }
        else{
             topics = topicRepository.findTop30ByCategoryIdAndIsUsedTrueOrderByUsedAtAsc(categoryId);
        }
        List<CategorySummaryDto> categories =categoryRepository.findAll().stream()
                .map(c->CategorySummaryDto.builder()
                        .categoryId(c.getId())
                .categoryName(c.getName())
                        .build())
                .toList();


         List<TopicCollectionResponse> topicCollectionResponses = topics.stream()
                .map(TopicCollectionResponse::from)
                .collect(Collectors.toList());

        //  없으면 null
        String categoryName = categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse(null);

        return new TopicListResponse(categories,categoryName,topicCollectionResponses);
    }

    @Transactional
    public void generateTopicsForCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        String categoryPrompt = switch (category.getName()) {
            case "일상" -> "일상: 매일 반복되는 습관, 공간, 감정의 변화를 관찰하고 의미를 찾는 주제";
            case "인간관계" -> "인간관계: 연애, 가족, 친구 사이의 심리, 소통, 갈등 해법을 탐구하는 주제 등 (예: 연인의 과거에 대한 태도 등)";
            case "문화·트렌드" -> "문화,트렌드:  SNS, 소비 트렌드, 최신 라이프스타일을 해석하는 주제 등등(예: 연애 프로그램 출연 선택 등)";
            case "가치관" -> "가치관: 삶의 우선순위, 행복의 기준, 도덕적 딜레마를 다루는 주제 등";
            case "시대·사회" -> "시대와 사회: AI 기술, 세대 갈등, 환경, 공정성 등 시대 및 사회의 변화를 비판적으로 사고하는 주제 등";
            default -> throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        };
        List<TopicGenerationResponse> aiTopics =topicAIService.generateTopics(categoryPrompt);

        // 신규 토픽 embedding 30개 한 번에 생성
        Map<String, List<Double>> newEmbeddingCache = new HashMap<>();
        for (TopicGenerationResponse t : aiTopics) {
            newEmbeddingCache.put(t.topicName(), topicAIService.getEmbedding(t.topicName()));
            try {
                Thread.sleep(250);  // 0.25초 딜레이
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 상태 복구
                throw new RuntimeException(e);
            }
        }

        // 3. 중복 제거 (embedding 재사용)
        List<TopicGenerationResponse> filtered =
                removeDuplicates(categoryId, aiTopics, newEmbeddingCache);

        List<Topic> entities = filtered.stream()
                .map(t -> Topic.create(
                        category,
                        t.topicName(),
                        TopicType.valueOf(t.topicType().toUpperCase()),
                        newEmbeddingCache.get(t.topicName())
                ))
                .toList();

        topicRepository.saveAll(entities);
        log.info("카테고리 [{}] 에 대해 주제 {}개 생성 및 저장 완료", category.getName(), entities.size());
    }

    @Transactional
    public Topic generateTopicForCategory(Long categoryId, String topicName ,TopicType topicType) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        //1.단일 토픽 임베딩 생성
        List<Double> newEmbedding = topicAIService.getEmbedding(topicName);

        //2. 임시 Topic 객체생성
        Topic newTopic = Topic.create(
                category,
                topicName,
                topicType,
                newEmbedding
        );

        // 3. 최근 토픽 40개 가져오기
        List<Topic> recentTopics =
                topicRepository.findTop40ByCategoryIdAndTopicStatusOrderByUpdatedAtDesc(categoryId, Status.APPROVED);

        if(isNotDuplicate(newEmbedding, recentTopics)) {
            Topic savedTopic = topicRepository.save(newTopic);
            log.info("카테고리 [{}] 수동 주제 [{}] 생성 및 저장 완료", category.getName(), topicName);
            return savedTopic;
        }else {
            // 중복일 경우 비즈니스 예외 발생 또는 null 반환 등 정책 결정
            log.warn("카테고리 [{}] 에 대해 주제 [{}]는 중복되어 저장하지 않음", category.getName(), topicName);
            throw new BusinessException(ErrorCode.DUPLICATE_TOPIC_NOT_ALLOWED);
        }

    }


    public List<TopicGenerationResponse> removeDuplicates(
            Long categoryId,
            List<TopicGenerationResponse> newTopics,
            Map<String, List<Double>> newEmbeddingCache) {

        // 최근 토픽 40개 가져오기
        List<Topic> recentTopics =
                topicRepository.findTop40ByCategoryIdAndTopicStatusOrderByUpdatedAtDesc(categoryId, Status.APPROVED);


        List<TopicGenerationResponse> result = new ArrayList<>();

        for (TopicGenerationResponse t : newTopics) {

            List<Double> newEmb = newEmbeddingCache.get(t.topicName());

            if (isNotDuplicate(newEmb, recentTopics)) {
                result.add(t);
                // 중복 없으면 recentTopics에 추가 (메모리 상)
                Topic fake = Topic.create(null, t.topicName(),
                        TopicType.valueOf(t.topicType().toUpperCase()),
                        newEmb);
                recentTopics.add(fake);
            }
            else{ log.info("유사 질문 : {}", t.topicName());}
        }
        return result;
    }

    private boolean isNotDuplicate(List<Double> newEmbedding, List<Topic> recentTopics) {

        for (Topic old : recentTopics) {
            List<Double> oldEmbedding = old.getEmbedding();
            double sim = cosineSimilarity(newEmbedding, oldEmbedding);
            if (sim >= 0.88) {
                log.info("DB에 존재한 질문 :{}", old.getName());
                return false;
            }
        }
        return true;
    }

    public double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0;
        double normA =0.0;
        double normB =0.0;

        for(int i=0;i<v1.size();i++){
            dot += v1.get(i)*v2.get(i);
            normA += Math.pow(v2.get(i),2);
            normB += Math.pow(v1.get(i),2);
        }
        return dot / (Math.sqrt(normA)*Math.sqrt(normB));
    }

    @Transactional
    public Topic updateTopic(Long topicId, TopicUpdateRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        if (request.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            topic.setCategory(newCategory);
        }
        topic.updateNameAndType(request.getTopicName(), request.getTopicType());

        return topic;
    }

    @Transactional
    public void updateReservation(Long topicId, LocalDate usedAt) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        // usedAt != null  예약
        // usedAt == null  예약 취소 (자동 픽 대상)
        Optional<Topic> checkTopic = topicRepository.findByUsedAtAndCategory_Id(usedAt,topic.getCategory().getId());
        if(checkTopic.isEmpty()){
            topic.reserveAt(usedAt);
        }
       else{
           throw new BusinessException(ErrorCode.DUPLICATE_TOPIC_DATE);
        }
    }

    @Transactional
    public void deleteTopic(Long topicId) {
        topicRepository.deleteById(topicId);
    }

    public HomeResponse getHome(Long userId) {
        StreakResponse streakCount = null;
        TodayPostResponse todayPostResponse = null;

        if (userId != null) {
            streakCount = userService.getStreak(userId);
            todayPostResponse = postService.findPostStatusByToday(userId);
        }

        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(category -> CategoryResponse.toDto(category.getId(), category.getName()))
                .toList();

        return HomeResponse.toDto(streakCount, categories, todayPostResponse);
    }

     //ADMIN 페이지 관련조회
    public AdminTopicListResponse getAdminTopics(String mode,Long categoryId) {

        List<Topic> topics = topicRepository.findAdminTopics(mode,categoryId);
        List<AdminTopicDto> adminTopics = topics.stream()
                .map(AdminTopicDto::from)
                .toList();

        return AdminTopicListResponse.of(adminTopics);
    }

    @Transactional
    public void updateTopicStatus(Long topicId,Status newStatus){
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(()->new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        if (topic.getTopicStatus() == newStatus) {
            return;
        }
        topic.setTopicStatus(newStatus);
    }

}
