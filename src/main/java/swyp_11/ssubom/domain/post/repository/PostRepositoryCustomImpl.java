package swyp_11.ssubom.domain.post.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import swyp_11.ssubom.domain.post.dto.MyPostRequestDto;
import swyp_11.ssubom.domain.post.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import swyp_11.ssubom.domain.post.entity.PostStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static swyp_11.ssubom.domain.post.entity.QPost.post;
import static swyp_11.ssubom.domain.topic.entity.QCategory.category;
import static swyp_11.ssubom.domain.topic.entity.QTopic.topic;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findMyPosts(Long userId, MyPostRequestDto request, Pageable pageable) {
        List<Post> content = queryFactory
                .selectFrom(post)
                .join(post.topic, topic).fetchJoin()
                .join(topic.category, category).fetchJoin()
                .where(
                        post.user.userId.eq(userId),
                        post.status.eq(PostStatus.PUBLISHED),
                        dateGoe(request.getStartDate()),
                        dateLoe(request.getEndDate())
                )
                .orderBy(orderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        post.user.userId.eq(userId),
                        post.status.eq(PostStatus.PUBLISHED),
                        dateGoe(request.getStartDate()),
                        dateLoe(request.getEndDate())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?>[] orderSpecifiers(Pageable pageable) {
        java.util.List<OrderSpecifier<?>> specs = new java.util.ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;
            switch (o.getProperty()) {
                case "updatedAt" -> specs.add(new OrderSpecifier<>(dir, post.updatedAt));
                case "createdAt" -> specs.add(new OrderSpecifier<>(dir, post.createdAt));
                case "postId"    -> specs.add(new OrderSpecifier<>(dir, post.postId));
                case "status"    -> specs.add(new OrderSpecifier<>(dir, post.status));
                default -> { }
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
}
