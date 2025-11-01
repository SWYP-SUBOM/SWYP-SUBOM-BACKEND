package swyp_11.ssubom.writing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.topic.entity.Topic;
import swyp_11.ssubom.user.entity.User;
import swyp_11.ssubom.writing.entity.Post;
import swyp_11.ssubom.writing.entity.PostStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * 1순위 테스트 (엔티티 POJO 테스트)
 * - DB, Spring, Mockito 아무것도 사용하지 않습니다.
 * - 순수 Post 객체(POJO)의 메서드가 의도대로 동작하는지만 검사합니다.
 */
class PostTest {

    // 테스트를 위한 '가짜' 의존 객체. new로 생성합니다.
    private User dummyUser;
    private Topic dummyTopic;

    @BeforeEach
    void setUp() {
        // Post.create()는 null을 허용하지 않으므로,
        // 실제 DB 객체가 아닌 'new'로 생성한 POJO 객체를 만듭니다.
        // 이 객체들은 어떤 ID도, 영속성도 갖지 않은 순수 Java 객체입니다.
        dummyUser = new User();
        dummyTopic = new Topic();
    }

    @Test
    @DisplayName("updateFromDraft: DRAFT -> PUBLISHED (발행) 테스트 (Case A)")
    void updateFromDraft_ToPublished_ShouldChangeStatusAndContent() {
        // given
        String originalContent = "이것은 초안입니다.";
        String newContent = "이것은 발행본입니다.";
        Post post = Post.create(dummyUser, dummyTopic, originalContent, PostStatus.DRAFT, "test-nick-A");

        // when
        // 핵심 로직: DRAFT 상태에서 PUBLISHED로 변경 요청
        post.updateFromDraft(PostStatus.PUBLISHED, newContent);

        // then
        // Post 객체의 '상태'가 기대한 대로 바뀌었는지 검사합니다.
        assertAll(
                () -> assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED),
                () -> assertThat(post.getContent()).isEqualTo(newContent),
                () -> assertThat(post.isRevised()).isFalse() // DRAFT -> PUBLISHED는 '수정'이 아님
        );
    }

    @Test
    @DisplayName("updateFromDraft: DRAFT -> DRAFT (임시저장) 테스트 (Case C)")
    void updateFromDraft_ToDraft_ShouldChangeOnlyContent() {
        // given
        String originalContent = "초안 1";
        String newContent = "초안 2 (수정)";
        Post post = Post.create(dummyUser, dummyTopic, originalContent, PostStatus.DRAFT, "test-nick-C");

        // when
        // 핵심 로직: DRAFT 상태에서 DRAFT로 변경 요청 (내용만 수정)
        post.updateFromDraft(PostStatus.DRAFT, newContent);

        // then
        assertAll(
                () -> assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT), // 상태는 DRAFT 유지
                () -> assertThat(post.getContent()).isEqualTo(newContent), // 내용만 변경
                () -> assertThat(post.isRevised()).isFalse()
        );
    }

    @Test
    @DisplayName("updateFromPublished: PUBLISHED -> PUBLISHED (수정) 테스트 (Case B)")
    void updateFromPublished_ToPublished_ShouldChangeContentAndSetRevised() {
        // given
        String originalContent = "발행본 1";
        String newContent = "발행본 1 (수정)";

        // 초기 상태를 PUBLISHED로 설정
        Post post = Post.create(dummyUser, dummyTopic, originalContent, PostStatus.PUBLISHED, "test-nick-B");
        assertThat(post.isRevised()).isFalse(); // 초기엔 false

        // when
        // 핵심 로직: PUBLISHED 상태에서 PUBLISHED로 변경 요청 (내용 수정)
        post.updateFromPublished(PostStatus.PUBLISHED, newContent);

        // then
        assertAll(
                () -> assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED),
                () -> assertThat(post.getContent()).isEqualTo(newContent),
                () -> assertThat(post.isRevised()).isTrue() // 수정됨(isRevised) 플래그가 true가 되어야 함
        );
    }

    @Test
    @DisplayName("updateFromPublished: PUBLISHED -> DRAFT (역행) 테스트 (Invalid)")
    void updateFromPublished_ToDraft_ShouldThrowException() {
        // given
        String originalContent = "발행본 1";
        String newContent = "초안으로 돌리기";
        Post post = Post.create(dummyUser, dummyTopic, originalContent, PostStatus.PUBLISHED, "test-nick-Invalid");

        // when & then
        // 핵심 로직: PUBLISHED 상태에서 DRAFT로 변경 요청
        assertThatThrownBy(() -> post.updateFromPublished(PostStatus.DRAFT, newContent))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_STATUS_TRANSITION.getErrorMessage());

        // 상태가 롤백되었거나 변경되지 않았는지 확인
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post.getContent()).isEqualTo(originalContent);
        assertThat(post.isRevised()).isFalse();
    }
}
