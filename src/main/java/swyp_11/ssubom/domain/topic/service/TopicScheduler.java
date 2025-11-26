package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicScheduler {
    private final TopicService topicService;
    private final TopicAIService topicAIService;
    private final CategoryRepository topicCategoryRepository;

    @Scheduled(cron = "0 0 0 * * *", zone="Asia/Seoul")
    public void dailyPick() {
        for(Long id=1L;id<=5;id++){
            topicService.ensureTodayPicked(id);
        }
        log.info("오늘의 질문 할당 완료!");
    }

    @Scheduled(cron = "10 0 20 * * *")
    public void generateTopics() {
        List<Category> categories = topicCategoryRepository.findAll();
        for(Category category : categories){
            Long categoryId = category.getId();
            topicService.generateTopics(categoryId);
    }
}}
