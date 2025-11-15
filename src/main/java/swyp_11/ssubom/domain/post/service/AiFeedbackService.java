package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    private static final int MIN_CONTENT_LENGTH = 100; // 최소 글자 수 정의

    @Transactional
    public AiFeedbackStartResponseDto startFeedbackGeneration(Long postId) {

        // 1. Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        String content = post.getContent(); // (post에서 content를 가져온다고 가정)

        // 2. [ ✨ 핵심 ✨ ] 비동기 호출 전에 글자 수 검증
        if (content == null || content.trim().length() < MIN_CONTENT_LENGTH) {
            // 400 Bad Request 에러를 즉시 발생시킴
            throw new BusinessException(ErrorCode.AIFEEDBACK_CONTENT_TOO_SHORT);
        }

        // 2. 'PROCESSING' 상태의 AIFeedback 엔티티 생성 및 저장
        AIFeedback feedback = AIFeedback.createProcessingFeedback(post, post.getContent());
        AIFeedback savedFeedback = aiFeedbackRepository.save(feedback);

        // 3. 비동기 작업자 호출 ((트랜잭션 분리)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                asyncGenerator.generateAndSaveFeedback(savedFeedback.getId(), savedFeedback.getContent());
            }
        });

        // 4. Controller에 즉시 응답 (ID, 'PROCESSING' 상태, errorMessage)
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
                feedback.getImprovementPoints(),
                feedback.getErrorMessage()
        );

        if (feedback.getStatus() == AIFeedbackStatus.COMPLETED) {
            dto.setStrength(feedback.getStrength());
            dto.setSummary(feedback.getSummary());
            dto.setImprovementPoints(feedback.getImprovementPoints());
        }
        return dto;

    }

    @Transactional
    public AiFeedbackResultResponseDto getAiFeedback(Long userId,Long postId, Long AiFeedbackId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
        }

        AIFeedback aiFeedback = aiFeedbackRepository.findById(AiFeedbackId)
                .orElseThrow(()-> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));

         if (aiFeedback.getStatus() == AIFeedbackStatus.COMPLETED) {
            return  new AiFeedbackResultResponseDto(
                    aiFeedback.getId(),
                    aiFeedback.getStatus(),
                    aiFeedback.getStrength(),
                    aiFeedback.getSummary(),
                    aiFeedback.getImprovementPoints(),
                    aiFeedback.getErrorMessage()
            );
        }
        else if (aiFeedback.getStatus() == AIFeedbackStatus.PROCESSING) {
                return AiFeedbackResultResponseDto.builder()
                        .aiFeedbackId(aiFeedback.getId())
                        .status(AIFeedbackStatus.PROCESSING)
                        .build();
        }
        else {
                 return AiFeedbackResultResponseDto.builder()
                         .aiFeedbackId(aiFeedback.getId())
                         .status(AIFeedbackStatus.FAILED)
                         .errorMessage(aiFeedback.getErrorMessage())
                         .build();
         }
    }
}
