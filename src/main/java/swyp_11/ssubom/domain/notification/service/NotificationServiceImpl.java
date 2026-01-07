package swyp_11.ssubom.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swyp_11.ssubom.domain.calendar.dto.CategoryInfo;
import swyp_11.ssubom.domain.notification.dto.NotificationItem;
import swyp_11.ssubom.domain.notification.dto.NotificationListResponse;
import swyp_11.ssubom.domain.notification.entity.Notification;
import swyp_11.ssubom.domain.notification.repository.NotificationRepository;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.ReactionType;
import swyp_11.ssubom.domain.post.repository.ReactionRepository;
import swyp_11.ssubom.domain.user.entity.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    //private static final long TIMEOUT = 1000L * 60 * 10; // 10분
    private static final long TIMEOUT = 1000L * 60 * 3; // 3분

    private final NotificationRepository notificationRepository;
    private final ReactionRepository reactionRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 로그인 시 SSE 연결
     */
    @Override
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(userId, emitter);
        log.info("[SSE 연결 성공] userId={}", userId);

        sendSnapshotAsync(userId, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("[SSE 연결 종료] userId={}", userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.info("[SSE 타임아웃] userId={}", userId);
        });

        emitter.onError(e -> {
            emitters.remove(userId);
            log.error("[SSE 에러] userId={}", userId, e);
        });
        return emitter;
    }

    /**
     * 반응 클릭 시 알림 발생
     * - 자기 글에 누른 경우는 알림 제외
     * - 기존 알림(같은 post + reactionType) 있으면 actorCount +1
     */
    @Override
    @Transactional
    public void createReactionNotification(Post post, User actor, ReactionType oldType, ReactionType newType) {
        User receiver = post.getUser();
        if (receiver.getUserId().equals(actor.getUserId())) return;

        // 1. 기존 반응(oldType) 알림 count 감소
        if (oldType != null && !oldType.equals(newType)) {
            notificationRepository.findByReceiverAndPostAndReactionType(receiver, post, oldType)
                    .ifPresent(oldNoti -> {
                        long oldCount = reactionRepository.countByPostAndType(post.getPostId(), oldType.getId());
                        if (oldCount <= 0) {
                            notificationRepository.delete(oldNoti);
                            log.debug("[알림 삭제] postId={}, reactionType={}", post.getPostId(), oldType.getName());
                        } else {
                            oldNoti.setActorCount(oldCount);
                            notificationRepository.save(oldNoti);
                        }
                    });
        }

        // 2. 새 반응(newType) 알림 count 증가
        if (newType != null) {
            Notification newNoti = notificationRepository
                    .findByReceiverAndPostAndReactionType(receiver, post, newType)
                    .orElseGet(() -> Notification.of(receiver, post, newType, 0L));

            long newCount = reactionRepository.countByPostAndType(post.getPostId(), newType.getId());
            newNoti.setActorCount(newCount);
            newNoti.markAsUnread();
            Notification savedNoti = notificationRepository.save(newNoti);

            // Transaction commit 후에 SSE 전송
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendNotificationAfterCommit(savedNoti);
                }
            });
        }
    }

    /**
     * 최초 연결 시 unreadCount 전달
     */
    private void sendSnapshotAsync(Long userId, SseEmitter emitter) {
        CompletableFuture.runAsync(() -> {
            try {
                long unreadCount = getUnreadCount(userId); // 트랜잭션 정상 작동

                sendEvent(emitter, "snapshot", Map.of("unreadCount", unreadCount));
                log.info("[SSE 초기 snapshot 전송 완료] userId={}, unread={}", userId, unreadCount);

            } catch (Exception e) {
                log.error("[SSE 초기 snapshot 전송 실패] userId={}", userId, e);
            }
        });
    }

    /**
     * Unread count 조회
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiver_UserIdAndIsReadFalse(userId);
    }

    /**
     * 새 알림 전송
     */
    private void sendNotificationAfterCommit(Notification notification) {
        Long userId = notification.getReceiver().getUserId();
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            long unreadCount = getUnreadCount(userId);

            Map<String, Object> data = Map.of(
                    "unreadCount", unreadCount,
                    "notification", toDto(notification)
            );

            sendEvent(emitter, "newNotification", data);
            log.info("[알림 전송] → userId={}, postId={}, unreadCount={}",
                    userId, notification.getPost().getPostId(), unreadCount);
        }
    }

    private Map<String, Object> toDto(Notification notification) {
        return Map.of(
                "id", notification.getId(),
                "postId", notification.getPost().getPostId(),
                "reactionType", notification.getReactionType(),
                "actorCount", notification.getActorCount(),
                "isRead", notification.isRead(),
                "createdAt", notification.getCreatedAt()
        );
    }

    private void sendEvent(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(event)
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    @Override
    @Transactional
    public NotificationListResponse getNotifications(Long userId, int limit, LocalDateTime cursor) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Notification> notifications = notificationRepository.findRecentNotifications(userId, cursor, pageable);

        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);

        boolean hasMore = hasMoreNotifications(notifications, limit);
        LocalDateTime nextCursor = getNextCursor(notifications, hasMore);

        List<NotificationItem> items = notifications.stream()
                .map(n -> NotificationItem.of(
                        n, CategoryInfo.of(n.getPost().getTopic().getCategory())
                ))
                .toList();

        return new NotificationListResponse(items, nextCursor, hasMore);
    }

    private boolean hasMoreNotifications(List<Notification> notifications, int limit) {
        return notifications.size() >= limit;
    }

    private LocalDateTime getNextCursor(List<Notification> notifications, boolean hasMore) {
        if (!hasMore || notifications.isEmpty()) return null;
        return notifications.get(notifications.size() - 1).getUpdatedAt();
    }
}
