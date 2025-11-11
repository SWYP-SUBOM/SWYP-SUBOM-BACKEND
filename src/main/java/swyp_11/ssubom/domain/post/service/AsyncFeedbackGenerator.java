package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.HyperClovaResponseDto;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.repository.AiFeedbackRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncFeedbackGenerator {
    private final AiFeedbackRepository aiFeedbackRepository;
    private final HyperClovaService hyperClovaService;

    @Async
    @Transactional
    public void generateAndSaveFeedback(Long aiFeedbackId, String content) {
        log.info("[Async Start] AI 피드백 생성 시작 (ID: {})", aiFeedbackId);
        AIFeedback feedback = aiFeedbackRepository.findById(aiFeedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));

        try {
            HyperClovaResponseDto responseDto = hyperClovaService.getFeedback(content);

            feedback.completeFeedback(
                    responseDto.getSummary(),
                    responseDto.getStrength(),
                    responseDto.getImprovementPoints()
            );
            log.info("[Async Success] AI 피드백 생성 완료 (ID: {})", aiFeedbackId);

        } catch (Exception e) {
            log.error("[Async Fail] AI 피드백 생성 중 오류 발생 (ID: {})", aiFeedbackId, e);
            feedback.failFeedback(e.getMessage());
        }
    }
}
