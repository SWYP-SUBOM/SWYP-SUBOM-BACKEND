package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.TodayPostResponse;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.post.repository.PostRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public TodayPostResponse findPostStatusByToday(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return postRepository
                .findFirstByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startOfDay, endOfDay)
                .map(post -> TodayPostResponse.toDto(post.getPostId(), post.getStatus()))
                .orElse(TodayPostResponse.toDto(null, PostStatus.NOT_STARTED));
    }
}
