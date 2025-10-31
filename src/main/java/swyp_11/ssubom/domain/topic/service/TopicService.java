package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.topic.dto.TodayTopicResponseDto;
import swyp_11.ssubom.domain.topic.dto.TopicCollectionResponse;
import swyp_11.ssubom.domain.topic.dto.TopicListResponse;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;

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
        Optional<Topic> existing = topicRepository.findByCategory_IdAndUsedTrueAndUsedAt(categoryId, today);
        if(existing.isPresent()) {
            return existing;
        }
        Topic t =topicRepository.lockOneUnused(categoryId);
        //다 씀
        if (t == null) return Optional.empty();
        t.setUsed(true);
        t.setUsedAt(today);
        return Optional.of(t);
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
         List<TopicCollectionResponse> t = topics.stream()
                .map(TopicCollectionResponse::from)
                .collect(Collectors.toList());

        String categoryName = topics.get(0).getCategory().getName();
        return new TopicListResponse(categoryName,t);
    }

}
