package swyp_11.ssubom.domain.topic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.TopicGeneration;
import swyp_11.ssubom.domain.topic.repository.CategoryRepository;
import swyp_11.ssubom.domain.topic.repository.TopicGenerationRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTopicGenerationWorker {
    private final TopicGenerationRepository topicGenerationRepository;
    private final CategoryRepository categoryRepository;
    private final TopicService topicService;

    @Async
    @Transactional
    public void generate(Long generationId){
        TopicGeneration tg = topicGenerationRepository.findById(generationId)
                .orElseThrow(()->new BusinessException(ErrorCode.TOPICGENERATIONID_NOT_FOUND));

        try {
            List<Category> categories = categoryRepository.findAll();


            boolean hasError = false;
            StringBuilder errorMessage = new StringBuilder();

            for (Category category : categories) {
                try {
                    topicService.generateTopicsForCategory(category.getId());
                } catch (Exception e) {
                    hasError = true;
                    errorMessage.append(" [카테고리- ").append(category.getName()).append(" 에러 : ]").append(e.getMessage()).append("\n");
                    log.error("카테고리 {} 토픽 생성실패 ", category.getName(), e);
                }
            }
            if (hasError) {
                tg.completeWithErrors(errorMessage.toString());
            } else {
                tg.complete();
            }
        }catch (Exception e){
            tg.fail("시스템 오류 : "+e.getMessage());
        }
    }
}
