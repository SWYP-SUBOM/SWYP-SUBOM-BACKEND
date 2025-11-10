package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.AiFeedbackResultResponseDto;
import swyp_11.ssubom.domain.post.dto.AiFeedbackStartResponseDto;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.entity.AIFeedbackStatus;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.repository.AiFeedbackRepository;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiFeedbackService {
    private final PostRepository postRepository;
    private final AiFeedbackRepository aiFeedbackRepository;
    private final AsyncFeedbackGenerator asyncGenerator;

    @Transactional
    public AiFeedbackStartResponseDto startFeedbackGeneration(Long postId) {

        // 1. Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 2. 'PROCESSING' 상태의 AIFeedback 엔티티 생성 및 저장
        AIFeedback feedback = AIFeedback.createProcessingFeedback(post, post.getContent());
        AIFeedback savedFeedback = aiFeedbackRepository.save(feedback);

        // 3. 비동기 작업자 호출 ((트랜잭션 분리)
        asyncGenerator.generateAndSaveFeedback(savedFeedback.getId(), savedFeedback.getContent());

        // 4. Controller에 즉시 응답 (ID, 'PROCESSING' 상태)
        return new AiFeedbackStartResponseDto(savedFeedback.getId(), savedFeedback.getStatus());
    }

    public AiFeedbackResultResponseDto getFeedbackResult(Long aiFeedbackId) {

        // 1. AIFeedback 엔티티 조회
        AIFeedback feedback = aiFeedbackRepository.findById(aiFeedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));

        // 2. DTO로 변환하여 반환
        return convertToResultDto(feedback);
    }

    private AiFeedbackResultResponseDto convertToResultDto(AIFeedback feedback) {
        AiFeedbackResultResponseDto dto = new AiFeedbackResultResponseDto(
                feedback.getId(),
                feedback.getStatus(),
                feedback.getStrength(),
                feedback.getSummary(),
                feedback.getImprovementPoints()
        );

        if (feedback.getStatus() == AIFeedbackStatus.COMPLETED) {
            dto.setStrength(feedback.getStrength());
            dto.setSummary(feedback.getSummary());
            dto.setImprovementPoints(feedback.getImprovementPoints());
        }
        return dto;

    }
}
