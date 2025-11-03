package swyp_11.ssubom.writing.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.user.repository.UserRepository;
import swyp_11.ssubom.domain.repository.TopicRepository;
import swyp_11.ssubom.domain.entity.Topic;
import swyp_11.ssubom.domain.entity.User;
import swyp_11.ssubom.writing.dto.*;
import swyp_11.ssubom.domain.entity.Post;
import swyp_11.ssubom.domain.entity.PostStatus;
import swyp_11.ssubom.domain.repository.PostRepository;
import swyp_11.ssubom.global.nickname.NicknameGenerator;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class WritingServiceImpl implements WritingService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;
    private final TopicRepository topicRepository;
    private static final int SERVICE_LEVEL_MAX_TRIES = 5;

    @Override
    public WritingCreateResponse createWriting(Long userId, WritingCreateRequest request) {
        User user = userRepository.findById(userId)



                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        for (int attempt = 0; attempt < SERVICE_LEVEL_MAX_TRIES; attempt++) {
            String generatedNickname = nicknameGenerator.generateNickname(userId);

            Post newPost = Post.create(user, topic, request.getContent(), PostStatus.DRAFT, generatedNickname);
            try {
                Post savedPost = postRepository.saveAndFlush(newPost);
                return WritingCreateResponse.of(savedPost);
            } catch (DataIntegrityViolationException e) {
                log.warn("Nickname conflict on attempt {} for nickname={}", attempt, generatedNickname, e);
            }
        }

        throw new BusinessException(ErrorCode.NICKNAME_GENERATION_FAILED);
    }

    @Override
    public WritingUpdateResponse updateWriting(Long userId, Long postId, WritingUpdateRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        PostStatus nextStatus = request.getStatus();

        post.update(nextStatus, request.getContent());

        return WritingUpdateResponse.of(post);
    }

    @Override
    public void deleteWriting(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_WRITING_MODIFICATION);
        }

        //PRD에 따르면 PUBLISHED deletion 불가
        if (post.getStatus() == PostStatus.PUBLISHED) {
            log.warn("Attempt to delete published post. postId: {}, userId: {}", postId, userId);
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PUBLISHED_POST);
        }

        postRepository.delete(post);
    }
}
