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
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;
import swyp_11.ssubom.domain.user.dto.StreakResponse;
import swyp_11.ssubom.domain.user.service.UserService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
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
                        t.getId()
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
