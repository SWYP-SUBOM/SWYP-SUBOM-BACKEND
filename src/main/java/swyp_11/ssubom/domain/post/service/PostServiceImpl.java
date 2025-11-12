package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.entity.*;
import swyp_11.ssubom.domain.post.repository.AiFeedbackRepository;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.post.repository.PostViewRepository;
import swyp_11.ssubom.domain.post.repository.ReactionRepository;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.topic.repository.TopicRepository;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.entity.Streak;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.StreakRepository;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.nickname.NicknameGenerator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private static final int SERVICE_LEVEL_MAX_TRIES = 5;
    private static final int DEFAULT_PAGE_SIZE = 15;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;
    private final TopicRepository topicRepository;
    private final ReactionRepository reactionRepository;
    private final PostViewRepository postViewRepository;
    private final StreakRepository streakRepository;
    private final AiFeedbackRepository aiFeedbackRepository;

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

        if (!post.isWrittenBy(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_WRITING_MODIFICATION);
        }

        PostStatus nextStatus = request.getStatus();
        post.update(nextStatus, request.getContent());

        if (nextStatus == PostStatus.PUBLISHED) {
            recordStreakProgress(post.getUser());
        }

        return PostUpdateResponse.of(post);
    }

    /**
     * 사용자가 글을 발행할 때 호출되어 스트릭(streak)과 챌린저(challenger) 정보를 갱신한다.
     * - 처음 작성하는 경우 streak save
     * - 하루 1회 이상 작성 시 streakCount 1 증가
     * - 주간 5회 이상 작성 시 challengerCount 증가
     */
    private void recordStreakProgress(User user) {
        LocalDate today = LocalDate.now();

        Streak streak = streakRepository.findByUser(user)
                .orElseGet(() -> streakRepository.save(Streak.create(user)));

        boolean hasPostedToday = postRepository.existsByUser_UserIdAndStatusAndUpdatedAtBetween(
                user.getUserId(), PostStatus.PUBLISHED,
                today.atStartOfDay(), today.atTime(LocalTime.MAX)
        );
        streak.increaseDaily(hasPostedToday);

        LocalDate startOfWeek = today.with(DayOfWeek.SUNDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        long weeklyPostCount = postRepository.countByUser_UserIdAndStatusAndUpdatedAtBetween(
                user.getUserId(), PostStatus.PUBLISHED,
                startOfWeek.atStartOfDay(), endOfWeek.atTime(LocalTime.MAX)
        );
        streak.updateWeeklyChallenge(weeklyPostCount);
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
        ReactionType reactionType = reactionRepository
                .findByPostAndUser(post, loginUser)
                .map(Reaction::getType)
                .orElse(null);

        MyReactionInfo myReaction = MyReactionInfo.of(reactionType);

        return PostDetailResponse.of(post, isMe, reactions, viewCount, myReaction);
    }

    @Override
    @Transactional
    public PostListResponseDto getPostList(Long categoryId,LocalDateTime cursorUpdatedAt,Long cursorPostId) {

        LocalDate today = LocalDate.now();
        Topic topic = topicRepository.findByUsedAtAndCategory_Id(today,categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        Long topicId = topic.getId();

        int limit = DEFAULT_PAGE_SIZE;
        List<Post> posts = postRepository.findPostsForInfiniteScroll(
                topicId,
                cursorUpdatedAt,
                cursorPostId,
                limit
        );

        boolean hasMore = posts.size() > limit;
        List<Post> actualPosts = hasMore ? posts.subList(0, limit) : posts;

        LocalDateTime nextUpdatedAt = null;
        Long nextPostId = null;

        if (hasMore) {
            Post cursorPost = posts.get(limit-1);
            nextUpdatedAt = cursorPost.getUpdatedAt();
            nextPostId = cursorPost.getPostId();
        }
        List<PostSummaryDto> postSummaryDtos=actualPosts.stream()
                .map(post ->{
                            AIFeedback aiFeedback = aiFeedbackRepository.findByPost_PostId(post.getPostId()).orElseThrow(
                                    ()-> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND)
                            );
                            Long reactionCount = reactionRepository.countByPost(post);
                            Long viewCount = postViewRepository.countByPost(post);
                            return PostSummaryDto.of(post, aiFeedback, reactionCount, viewCount);
                        }
                       ).collect(Collectors.toList());

        return PostListResponseDto.from(topic,postSummaryDtos,nextUpdatedAt, nextPostId, hasMore);
    }

}
