package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.TopicGeneration;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicGenerationRepository;

import java.util.List;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicGenerationService {
    private final TopicService topicService;
    private final TopicGenerationRepository topicGenerationRepository;
    private final AsyncTopicGenerationWorker asyncWorker;

    @Scheduled(cron = "30 26 0 * * *", zone="Asia/Seoul")
    public void dailyPick() {
        for(Long id=1L;id<=5;id++){
            topicService.ensureTodayPicked(id);
        }
        log.info("오늘의 질문 할당 완료!");
    }

    //시작 기록 남기기
    @Transactional
    public TopicGeneration startGeneration(){
        TopicGeneration tg = topicGenerationRepository.save(
                TopicGeneration.start()
        );
         //비동기실행
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        asyncWorker.generate(tg.getId());
                    }
                }
        );
        log.info(" ---- 주제 생성 작업번호 {} 완료 ------",tg.getId());
        return tg;
    }

    @Transactional(readOnly = true)
    public TopicGeneration getGeneration(Long generationId){
        return topicGenerationRepository.findById(generationId)
                .orElseThrow(()->new BusinessException(ErrorCode.TOPICGENERATIONID_NOT_FOUND));
    }

}
