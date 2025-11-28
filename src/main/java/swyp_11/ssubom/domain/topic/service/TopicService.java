package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.TodayPostResponse;
import swyp_11.ssubom.domain.post.service.PostService;
import swyp_11.ssubom.domain.topic.dto.*;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.entity.TopicType;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;
import swyp_11.ssubom.domain.user.dto.StreakResponse;
import swyp_11.ssubom.domain.user.service.UserService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.time.LocalDate;
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
        Optional<Topic> existing = topicRepository.findByCategory_IdAndIsUsedTrueAndUsedAt(categoryId, today);
        if(existing.isPresent()) {
            return existing;
        }
        Topic topic =topicRepository.lockOneUnused(categoryId);
        //다 씀
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
             topics = topicRepository.findTop30ByCategoryIdAndUsedAtIsNotNullOrderByUsedAtDesc(categoryId);
        }
        else{
             topics = topicRepository.findTop30ByCategoryIdAndUsedAtIsNotNullOrderByUsedAtAsc(categoryId);
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

        //1. 새 topic 30개 생성
        List<TopicGenerationResponse> aiTopics =topicAIService.generateTopics(category.getName());

        // 2. 신규 토픽 embedding 30개 한 번에 생성
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

    public List<TopicGenerationResponse> removeDuplicates(
            Long categoryId,
            List<TopicGenerationResponse> newTopics,
            Map<String, List<Double>> newEmbeddingCache) {

        // 최근 토픽 30개 가져오기
        List<Topic> recentTopics =
                topicRepository.findTop30ByCategoryIdAndUsedAtIsNotNullOrderByUsedAtDesc(categoryId);

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
            if (sim >= 0.9) {
                log.info(old.getName());
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
}
