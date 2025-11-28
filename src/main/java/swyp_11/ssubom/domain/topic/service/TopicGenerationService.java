package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicGenerationService {
    private final TopicService topicService;
    private final CategoryRepository categoryRepository;

    @Scheduled(cron = "0 0 0 * * *", zone="Asia/Seoul")
    public void dailyPick() {
        for(Long id=1L;id<=5;id++){
            topicService.ensureTodayPicked(id);
        }
        log.info("오늘의 질문 할당 완료!");
    }


    public void generateTopics() {
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            try {
                log.info("카테고리 [{}] 주제 생성 시작", category.getName());
                topicService.generateTopicsForCategory(category.getId());
                log.info("카테고리 [{}] 주제 생성 완료", category.getName());
            } catch (Exception e) {
                log.error(" 카테고리 [{}] 주제 생성 실패", category.getName(), e);
            }
        }
        log.info("===  주제 생성 스케줄러 종료 ===");
    }
}
