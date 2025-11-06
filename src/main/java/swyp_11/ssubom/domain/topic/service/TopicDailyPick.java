package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicDailyPick {
    private final TopicService topicService;

    @Scheduled(cron = "0 0 0 * * *", zone="Asia/Seoul")
    public void dailyPick() {
        for(Long id=1L;id<=5;id++){
            topicService.ensureTodayPicked(id);
        }
        log.info("오늘의 질문 할당 완료!");
    }
}
