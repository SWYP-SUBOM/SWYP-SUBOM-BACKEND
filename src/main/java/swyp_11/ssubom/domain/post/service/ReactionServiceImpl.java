package swyp_11.ssubom.domain.post.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.notification.service.NotificationService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.post.dto.ReactionMetricsDto;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.domain.post.dto.ReactionResponse;
import swyp_11.ssubom.domain.post.dto.ReactionUpsertRequest;
import swyp_11.ssubom.domain.post.entity.Reaction;
import swyp_11.ssubom.domain.post.entity.ReactionType;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.post.repository.ReactionRepository;
import swyp_11.ssubom.domain.post.repository.ReactionTypeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionTypeRepository reactionTypeRepository;
    private final ReactionRepository reactionRepository;
    private final NotificationService notificationService;

    @Override
    public ReactionResponse upsertReaction(Long userId, Long postId, ReactionUpsertRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        String reactionTypeName = request.getReactionTypeName();
        ReactionType reactionType = reactionTypeRepository.findByName(reactionTypeName);
        if (reactionType == null) {
            throw new BusinessException(ErrorCode.INVALID_REACTION_TYPE);
        }
        Optional<Reaction> currentUserReaction = reactionRepository.findByPostAndUser(post, user);

        Reaction reaction;
        ReactionType oldType = null;
        if (currentUserReaction.isPresent()) {
            reaction = currentUserReaction.get();
            oldType = reaction.getType();
            reaction.addType(reactionType);
        } else {
            reaction = Reaction.create(user, post, reactionType);
            reactionRepository.save(reaction);
        }

        notificationService.createReactionNotification(post, user, oldType, reactionType);

        //집계
        ReactionMetricsDto metrics = calculateReactionMetrics(post);
        return new ReactionResponse(
                post.getPostId(),
                metrics,
                reaction.getType().getName()
        );
    }

    @Override
    public ReactionResponse deleteReaction(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Reaction reaction = reactionRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.REACTION_NOT_FOUND));

        reactionRepository.delete(reaction);

        ReactionType deletedType = reaction.getType();
        notificationService.createReactionNotification(post, user, deletedType, null);

        ReactionMetricsDto metrics = calculateReactionMetrics(post);

        return new ReactionResponse(
                post.getPostId(),
                metrics,
                null
        );
    }

    private ReactionMetricsDto calculateReactionMetrics(Post post) {
        List<Object[]> countsByTypeRaw = reactionRepository.findReactionCountsByPost(post);
        Map<String, Long> countsByType = countsByTypeRaw.stream()
                .collect(Collectors.toMap(
                        entry -> (String) entry[0], // ReactionType 이름
                        entry -> (Long) entry[1]    // 개수
                ));

        Long totalReactionCounts = countsByType.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        return new ReactionMetricsDto(totalReactionCounts, countsByType);
    }
}
