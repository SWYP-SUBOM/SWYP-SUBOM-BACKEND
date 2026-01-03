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
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.utils.SentenceSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        String content = post.getContent();

        Topic topic = post.getTopic();

        // 2. 비동기 호출 전에 글자 수 검증
        if (content == null || content.trim().length() < MIN_CONTENT_LENGTH) {
            // 400 Bad Request 에러를 즉시 발생시킴
            throw new BusinessException(ErrorCode.AIFEEDBACK_CONTENT_TOO_SHORT);
        }

        // 2. 'PROCESSING' 상태의 AIFeedback 엔티티 생성 및 저장
        AIFeedback feedback = AIFeedback.createProcessingFeedback(post, post.getContent());
        AIFeedback savedFeedback = aiFeedbackRepository.save(feedback);

        // 3. 비동기 작업자 호출 (트랜잭션 분리)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                asyncGenerator.generateAndSaveFeedback(savedFeedback.getId(), savedFeedback.getContent(), topic.getTopicType(), topic.getCategory().getName(), topic.getName());
            }
        });

        // 4. Controller에 즉시 응답 (ID, 'PROCESSING' 상태, errorMessage)
        return new AiFeedbackStartResponseDto(savedFeedback.getId(), savedFeedback.getStatus());
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
             List<String> originalSentences = SentenceSplitter.split(aiFeedback.getContent());
             List<AiFeedbackResultResponseDto.FeedbackPointDto> pointDtos = aiFeedback.getImprovementPoints().stream()
                     .map(point -> {
                         String originalText = null;
                         int idx = point.getSentenceIndex();

                         if (idx >= 0 && idx < originalSentences.size()) {
                             originalText = originalSentences.get(idx);
                         }

                         return AiFeedbackResultResponseDto.FeedbackPointDto.builder()
                                 .reason(point.getReason())
                                 .sentenceIndex(idx)
                                 .originalText(originalText)
                                 .build();
                     })
                     .collect(Collectors.toList());
             return AiFeedbackResultResponseDto.builder()
                     .aiFeedbackId(aiFeedback.getId())
                     .status(AIFeedbackStatus.COMPLETED)
                     .strength(aiFeedback.getStrength())
                     .summary(aiFeedback.getSummary())
                     .grade(aiFeedback.getGrade().toString())
                     .improvementPoints(pointDtos)
                     .build();
        }
        else if (aiFeedback.getStatus() == AIFeedbackStatus.PROCESSING) {
                return AiFeedbackResultResponseDto.builder()
                        .aiFeedbackId(aiFeedback.getId())
                        .status(AIFeedbackStatus.PROCESSING)
                        .improvementPoints(new ArrayList<>())
                        .build();
        }
        else {
                 return AiFeedbackResultResponseDto.builder()
                         .aiFeedbackId(aiFeedback.getId())
                         .status(AIFeedbackStatus.FAILED)
                         .errorMessage(aiFeedback.getErrorMessage())
                         .improvementPoints(new ArrayList<>())
                         .build();
         }
    }
}
