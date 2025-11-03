package swyp_11.ssubom.domain.topic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.topic.dto.CategorySummaryDto;
import swyp_11.ssubom.domain.topic.dto.TodayTopicResponseDto;
import swyp_11.ssubom.domain.topic.dto.TopicCollectionResponse;
import swyp_11.ssubom.domain.topic.dto.TopicListResponse;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

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
                        t.getName()
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
        List<CategorySummaryDto> categories =List.of(
                CategorySummaryDto.builder().categoryId(1L).categoryName("문화&트렌드").build(),
                CategorySummaryDto.builder().categoryId(2L).categoryName("취미&취향").build(),
                CategorySummaryDto.builder().categoryId(3L).categoryName("가치관").build(),
                CategorySummaryDto.builder().categoryId(4L).categoryName("일상").build(),
                CategorySummaryDto.builder().categoryId(5L).categoryName("인간관계").build()
        );

         List<TopicCollectionResponse> t = topics.stream()
                .map(TopicCollectionResponse::from)
                .collect(Collectors.toList());

        String categoryName = topics.get(0).getCategory().getName();
        return new TopicListResponse(categories,categoryName,t);
    }

}
