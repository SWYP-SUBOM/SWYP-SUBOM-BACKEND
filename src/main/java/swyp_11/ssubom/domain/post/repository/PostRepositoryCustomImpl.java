package swyp_11.ssubom.domain.post.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import swyp_11.ssubom.domain.post.dto.MyPostRequestDto;
import swyp_11.ssubom.domain.post.dto.MyReactedPostRequestDto;
import swyp_11.ssubom.domain.post.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.post.entity.Reaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static swyp_11.ssubom.domain.post.entity.QPost.post;
import static swyp_11.ssubom.domain.topic.entity.QCategory.category;
import static swyp_11.ssubom.domain.topic.entity.QTopic.topic;
import static swyp_11.ssubom.domain.post.entity.QReaction.reaction;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Post> findMyPosts(Long userId, MyPostRequestDto request, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        List<Post> content = queryFactory
                .selectFrom(post)
                .join(post.topic, topic).fetchJoin()
                .join(topic.category, category).fetchJoin()
                .leftJoin(post.aiFeedback).fetchJoin()
                .where(
                        post.user.userId.eq(userId),
                        post.status.eq(PostStatus.PUBLISHED),
                        dateGoe(request.getStartDate()),
                        dateLoe(request.getEndDate()),
                        cursorCondition(request.getCursorId(), pageable.getSort())
                )
                .orderBy(orderSpecifiers(pageable))
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = content.size() > pageSize;
        if (hasNext) {
            content.remove(pageSize); //마지막꺼 제거
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Reaction> findMyReactedPosts(Long userId, MyReactedPostRequestDto request, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        List<Reaction> content = queryFactory
                .selectFrom(reaction)
                .join(reaction.post, post).fetchJoin()
                .join(post.topic, topic).fetchJoin()
                .join(topic.category, category).fetchJoin()
                .leftJoin(post.aiFeedback).fetchJoin()
                .where(
                        reaction.user.userId.eq(userId),
                        reaction.post.status.eq(PostStatus.PUBLISHED),
                        dateGoe(request.getStartDate()),
                        dateLoe(request.getEndDate()),
                        cursorCondition(request.getCursorId(), pageable.getSort())
                )
                .orderBy(orderSpecifiers(pageable))
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = content.size() > pageSize;
        if (hasNext) {
            content.remove(pageSize); // 마지막 데이터 제거
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private OrderSpecifier<?>[] orderSpecifiers(Pageable pageable) {
        java.util.List<OrderSpecifier<?>> specs = new java.util.ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;
            if ("updatedAt".equals(o.getProperty())) {
                specs.add(new OrderSpecifier<>(dir, post.updatedAt));
                specs.add(new OrderSpecifier<>(dir, post.postId)); // uniqueness보장
            }
        }
        return specs.toArray(new OrderSpecifier<?>[0]);
    }

    private BooleanExpression dateGoe(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }
        return post.updatedAt.goe(startDate.atStartOfDay());
    }

    private BooleanExpression dateLoe(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }
        return post.updatedAt.loe(endDate.atTime(LocalTime.MAX));
    }
    //cursor기반 paging을 위한 WHERE condition 생성
    private BooleanExpression cursorCondition(Long cursorId, Sort sort) {
        if (cursorId == null || cursorId == 0L) {
            return null; // 첫 페이지 조회
        }

        // 정렬 조건 확인 (updatedAt 기준)
        Sort.Order order = sort.getOrderFor("updatedAt");
        if (order == null) {
            return null;
        }

        var cursorUpdatedAt = com.querydsl.jpa.JPAExpressions
                .select(post.updatedAt)
                .from(post)
                .where(post.postId.eq(cursorId));

        if (order.isAscending()) { // 오래된순 (ASC)
            // (updatedAt > cursor.updatedAt) OR (updatedAt = cursor.updatedAt AND postId > cursor.postId)
            return post.updatedAt.gt(cursorUpdatedAt)
                    .or(post.updatedAt.eq(cursorUpdatedAt).and(post.postId.gt(cursorId)));
        } else { // 최신순 (DESC)
            // (updatedAt < cursor.updatedAt) OR (updatedAt = cursor.updatedAt AND postId < cursor.postId)
            return post.updatedAt.lt(cursorUpdatedAt)
                    .or(post.updatedAt.eq(cursorUpdatedAt).and(post.postId.lt(cursorId)));
        }
    }

    @Override
    public List<Post> findPostsForInfiniteScroll(Long topicId, LocalDateTime cursorUpdatedAt, Long cursorPostId, int limit) {
        BooleanExpression cursorCondition = null;
        if (cursorUpdatedAt != null && cursorPostId != null) {
            cursorCondition = post.updatedAt.lt(cursorUpdatedAt)
                    .or(post.updatedAt.eq(cursorUpdatedAt)
                            .and(post.postId.lt(cursorPostId)));}
        return queryFactory
                .selectFrom(post)
                .join(post.topic, topic).fetchJoin()
                .join(topic.category, category)
                .where(post.topic.id.eq(topicId),
                        post.status.eq(PostStatus.PUBLISHED),
                        cursorCondition
                )
                .orderBy(post.updatedAt.desc(), post.postId.desc())
                .limit(limit+1)
                .fetch();
    }
}




