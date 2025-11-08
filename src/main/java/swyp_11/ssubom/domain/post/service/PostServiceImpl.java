package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.post.entity.PostView;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.post.repository.PostViewRepository;
import swyp_11.ssubom.domain.post.repository.ReactionRepository;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.nickname.NicknameGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private static final int SERVICE_LEVEL_MAX_TRIES = 5;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;
    private final TopicRepository topicRepository;
    private final ReactionRepository reactionRepository;
    private final PostViewRepository postViewRepository;

    @Override
    @Transactional
    public PostCreateResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        for (int attempt = 0; attempt < SERVICE_LEVEL_MAX_TRIES; attempt++) {
            String generatedNickname = nicknameGenerator.generateNickname(userId);

            Post newPost = Post.create(user, topic, request.getContent(), PostStatus.DRAFT, generatedNickname);
            try {
                Post savedPost = postRepository.saveAndFlush(newPost);
                return PostCreateResponse.of(savedPost);
            } catch (DataIntegrityViolationException e) {
                log.warn("Nickname conflict on attempt {} for nickname={}", attempt, generatedNickname, e);
            }
        }

        throw new BusinessException(ErrorCode.NICKNAME_GENERATION_FAILED);
    }

    @Override
    @Transactional
    public PostUpdateResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        PostStatus nextStatus = request.getStatus();

        post.update(nextStatus, request.getContent());

        return PostUpdateResponse.of(post);
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_WRITING_MODIFICATION);
        }

        //PRD에 따르면 PUBLISHED deletion 불가
        if (post.getStatus() == PostStatus.PUBLISHED) {
            log.warn("Attempt to delete published post. postId: {}, userId: {}", postId, userId);
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PUBLISHED_POST);
        }

        postRepository.delete(post);
    }

    @Override
    public TodayPostResponse findPostStatusByToday(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return postRepository
                .findFirstByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startOfDay, endOfDay)
                .map(post -> TodayPostResponse.toDto(post.getPostId(), post.getStatus()))
                .orElse(TodayPostResponse.toDto(null, PostStatus.NOT_STARTED));
    }

    @Override
    @Transactional
    public PostDetailResponse getPostDetail(CustomOAuth2User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        boolean isMe = post.isWrittenBy(user.getUserId());

        // TODO: 현재 한 유저가 여러번 글 조회 시 조회수 증가하는 로직, 이후 수정 필요
        User loginUser = user.toEntity();
        postViewRepository.save(PostView.create(loginUser, post));

        List<PostReactionInfo> reactions = reactionRepository.countReactionsByPostId(postId);
        Long viewCount = postViewRepository.countByPost(post);

        return PostDetailResponse.of(post, isMe, reactions, viewCount);
    }
}
