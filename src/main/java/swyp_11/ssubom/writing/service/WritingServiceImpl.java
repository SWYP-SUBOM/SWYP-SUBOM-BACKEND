package swyp_11.ssubom.writing.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.security.repository.UserRepository;
import swyp_11.ssubom.topic.entity.Topic;
import swyp_11.ssubom.user.entity.User;
import swyp_11.ssubom.writing.entity.Post;
import swyp_11.ssubom.writing.dto.WritingCreateRequest;
import swyp_11.ssubom.writing.dto.WritingCreateResponse;
import swyp_11.ssubom.writing.repository.PostRepository;
import swyp_11.ssubom.writing.entity.AIFeedback;
import swyp_11.ssubom.writing.service.nickname.NicknameGenerator;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class WritingServiceImpl implements WritingService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final AIFeedbackRepository aiFeedbackRepository;
    private final NicknameGenerator nicknameGenerator;
    private static final int SERVICE_LEVEL_MAX_TRIES = 5;

    @Override
    public WritingCreateResponse createWriting(Long userId, WritingCreateRequest request) {
        // 1. validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        AIFeedback feedBack = null;

        if (request.getAiFeedbackId() != null) {
            feedBack = aiFeedbackRepository.findById(request.getAiFeedbackId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));
        }

        for (int attempt = 0; attempt < SERVICE_LEVEL_MAX_TRIES; attempt++) {
            String generatedNickname = nicknameGenerator.generateNickname(userId);

            Post newPost = Post.builder()
                    .user(user)
                    .topic(topic)
                    .content(request.getContent())
                    .status(request.getStatus())
                    .nickname(generatedNickname)
                    .build();
            try {
                Post savedPost = postRepository.saveAndFlush(newPost);
                return WritingCreateResponse.of(savedPost);
            } catch (DataIntegrityViolationException e) {
                log.warn("Nickname conflict on attempt {} for nickname={}", attempt, generatedNickname, e);
            }
        }

        throw new BusinessException(ErrorCode.NICKNAME_GENERATION_FAILED);
    }
}
