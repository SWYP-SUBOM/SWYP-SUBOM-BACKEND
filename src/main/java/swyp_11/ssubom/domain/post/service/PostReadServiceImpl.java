package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.entity.ImprovementPoint;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.Reaction;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.post.repository.ReactionRepository;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.Topic;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostReadServiceImpl implements PostReadService {
    private final PostRepository postRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    @Override
    public MyPostResponseDto getMyPosts(Long userId, MyPostRequestDto request) {
        Pageable pageable = createMyPostsPageable(request);
        Page<Post> postPage = postRepository.findMyPosts(userId, request, pageable);
        List<MyPostItem> items = postPage.getContent().stream()
                .map(this::convertPostToMyPostItem)
                .toList();

        PageInfoDto pageInfo = new PageInfoDto(
                postPage.getNumber() + 1,
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.isLast()
        );
        return new MyPostResponseDto(items, pageInfo);
    }

    @Override
    public MyReactedPostResponseDto getMyReactedPost(Long userId, MyReactedPostRequestDto request) {

        Pageable pageable = createMyReactedPostsPageable(request);
        Page<Reaction> reactionPage = postRepository.findMyReactedPosts(userId, request, pageable);
        List<Post> posts = reactionPage.getContent().stream()
                .map(Reaction::getPost)
                .toList();

        Map<Long, ReactionMetricsDto> metricsMap =
                reactionRepository.findReactionCountsByPosts(posts)
                        .stream()
                        .collect(Collectors.groupingBy(
                                PostReactionCountDto::getPostId,
                                Collectors.collectingAndThen(
                                        toList(),
                                        this::createMetricsDto
                                )
                        ));
        List<MyReactedPostItem> items = reactionPage.getContent().stream()
                .map(reaction -> convertReactionToMyReactedPostItem(reaction, metricsMap))
                .toList();

        PageInfoDto pageInfo = new PageInfoDto(
                reactionPage.getNumber() + 1,
                reactionPage.getSize(),
                reactionPage.getTotalPages(),
                reactionPage.getTotalElements(),
                reactionPage.isLast()
        );

        return new MyReactedPostResponseDto(items, pageInfo);
    }

    @Override
    public MyPostDetailResponseDto getMyPostDetail(CustomOAuth2User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.USER_MISMATCH);
        }
        Topic topic = post.getTopic();
        Category category = topic.getCategory();
        TopicInfo topicInfo = new TopicInfo(topic.getName(), category.getName());

        AiFeedbackInfo aiFeedbackInfo = null;
        AIFeedback aiFeedback = post.getAiFeedback(); // null가능
        if (aiFeedback != null) {
            List<ImprovementPoint> improvementPoints = aiFeedback.getImprovementPoints();
            aiFeedbackInfo = new AiFeedbackInfo(
                    aiFeedback.getId(),
                    aiFeedback.getStrength(),
                    improvementPoints.stream().map(ImprovementPoint::getContent).toList()
            );
        }

        return new MyPostDetailResponseDto(
                post.getPostId(),
                topic.getId(),
                post.getNickname(),
                topicInfo,
                post.getContent(),
                post.getStatus().toString(),
                post.getUpdatedAt(),
                post.isRevised(),
                aiFeedbackInfo
        );
    }
    // ------- my-writings -------
    private Pageable createMyPostsPageable(MyPostRequestDto request) {
        int page = request.getPage() - 1;
        int size = request.getSize();
        Sort sort;
        if (request.getSort().equals("oldest")) {
            sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        return PageRequest.of(page, size, sort);
    }


    private MyPostItem convertPostToMyPostItem(Post post) {
        Topic topic = post.getTopic();
        Category category = topic.getCategory();
        TopicInfo topicInfo = new TopicInfo(topic.getName(), category.getName());
        AIFeedback aiFeedback = post.getAiFeedback();
        String summary = (aiFeedback != null) ? aiFeedback.getSummary() : "api/ai-feedback구현 안해서 아직 피드백 없음"; //TODO: ai-feedback 구현 후 다시 테스트 필요

        return new MyPostItem(
                post.getPostId(),
                topicInfo,
                summary,
                post.getStatus().toString(),
                post.isRevised(),
                post.getUpdatedAt()
        );
    }

    //------- my-reactions -------

    private Pageable createMyReactedPostsPageable(MyReactedPostRequestDto request) {
        int page = request.getPage() - 1;
        int size = request.getSize();
        Sort sort;
        if (request.getSort().equals("oldest")) {
            sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        return PageRequest.of(page, size, sort);
    }

    private MyReactedPostItem convertReactionToMyReactedPostItem(
            Reaction reaction, Map<Long, ReactionMetricsDto> metricsMap
    ) {
        Post post = reaction.getPost();
        Reaction currentUserReaction = reaction;
        Topic topic = post.getTopic();
        Category category = topic.getCategory();
        TopicInfo topicInfo = new TopicInfo(topic.getName(), category.getName());
        AIFeedback aiFeedback = post.getAiFeedback();
        String summary = (aiFeedback != null) ? aiFeedback.getSummary() : "api/ai-feedback구현 안해서 아직 피드백 없음"; //TODO: ai-feedback 구현 후 다시 테스트 필요

        ReactionMetricsDto metrics = metricsMap.getOrDefault(
                post.getPostId(),
                new ReactionMetricsDto(0L, Map.of())
        );

        ReactionInfo reactionInfo = new ReactionInfo(currentUserReaction.getType().getName(), metrics);

        return new MyReactedPostItem(
                post.getPostId(),
                topicInfo,
                summary,
                reactionInfo,
                post.getStatus().toString(),
                post.isRevised(),
                post.getUpdatedAt()
        );
    }

    private ReactionMetricsDto createMetricsDto(List<PostReactionCountDto> counts) {
        Map<String, Long> countsByType = counts.stream()
                .collect(
                    Collectors.toMap(
                        PostReactionCountDto::getTypeName,
                        PostReactionCountDto::getCount
                ));
        Long total = counts.stream().mapToLong(PostReactionCountDto::getCount).sum();
        return new ReactionMetricsDto(total, countsByType);
    }

}
