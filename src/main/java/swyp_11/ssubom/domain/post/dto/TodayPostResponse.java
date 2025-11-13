package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.PostStatus;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.Topic;

@Getter
public class TodayPostResponse {
    private Long postId;
    private String postStatus;
    private Long categoryId;
    private String categoryName;
    private Long topicId;
    private String topicName;

    @Builder
    public TodayPostResponse(Long postId, String postStatus, Long categoryId, String categoryName, Long topicId, String topicName) {
        this.postId = postId;
        this.postStatus = postStatus;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public static TodayPostResponse toDto(Post post) {
        if(post == null) {
            return TodayPostResponse.builder()
                    .postId(null)
                    .postStatus(PostStatus.NOT_STARTED.name())
                    .categoryId(null)
                    .categoryName(null)
                    .topicId(null)
                    .topicName(null)
                    .build();
        }

        Topic topic = post.getTopic();
        Category category = topic.getCategory();
        return TodayPostResponse.builder()
                .postId(post.getPostId())
                .postStatus(post.getStatus().name())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .topicId(topic.getId())
                .topicName(topic.getName())
                .build();
    }
}
