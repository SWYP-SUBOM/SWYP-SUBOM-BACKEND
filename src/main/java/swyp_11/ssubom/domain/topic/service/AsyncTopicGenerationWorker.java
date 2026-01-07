package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicGenerationRepository;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTopicGenerationWorker {
    private final TopicGenerationRepository topicGenerationRepository;
    private final CategoryRepository categoryRepository;
    private final TopicService topicService;
    private final TopicStatusService statusService;

    @Async
    public void generate(Long generationId){

        try {
            List<Category> categories = categoryRepository.findAll();
            boolean hasError = false;
            StringBuilder errorMessage = new StringBuilder();

            for (Category category : categories) {
                try {
                    topicService.generateTopicsForCategory(category.getId());
                } catch (Exception e) {
                    hasError = true;
                    errorMessage.append(" [카테고리-").append(category.getName()).append("에러]").append(e.getMessage()).append("\n");
                    log.error("카테고리 {} 토픽 생성실패 ", category.getName(), e);
                }
            }
            // 별도 트랜잭션 사용 수정
            if (hasError) {
                statusService.updateToCompleteWithErrors(generationId, errorMessage.toString());
            } else {
                statusService.updateToSuccess(generationId);
            }
        }catch (Exception e){
            log.error("비동기 작업 중  시스템 오류", e);
            statusService.updateToFail(generationId, e.getMessage());
        }
    }
}
