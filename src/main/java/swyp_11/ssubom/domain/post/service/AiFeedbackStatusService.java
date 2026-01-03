package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.repository.AiFeedbackRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFeedbackStatusService {
    private final AiFeedbackRepository aiFeedbackRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(Long aiFeedbackId, String errorMessage) {
        log.warn("[FAIL-TX] AI 피드백(ID: {}) 상태 FAILED로 변경 시도.", aiFeedbackId);
        try {
            AIFeedback feedback = aiFeedbackRepository.findById(aiFeedbackId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));

            if (feedback != null) {
                feedback.failFeedback(errorMessage);
            }
        } catch (Exception e) {
            log.error("[FAIL-TX] FAILED 상태 변경 중 심각한 오류 발생 (ID: {})", aiFeedbackId, e);
        }
    }
}
