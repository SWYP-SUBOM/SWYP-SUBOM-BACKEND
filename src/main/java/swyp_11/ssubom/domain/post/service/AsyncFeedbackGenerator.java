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


        if (content == null || content.trim().length() < 100) {
            AIFeedback feedback = aiFeedbackRepository.findById(aiFeedbackId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));

            log.warn("[Async Fail] 내용이 너무 짧아 AI 피드백을 생성할 수 없습니다. (ID: {})", aiFeedbackId);
            feedback.failFeedback("피드백을 생성하기에 글이 너무 짧습니다."); // '실패' 상태로 변경
            return; // AI 호출하지 않고 종료
        }

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
