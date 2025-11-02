package swyp_11.ssubom.writing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swyp_11.ssubom.user.entity.User;
import swyp_11.ssubom.writing.entity.Post;
import swyp_11.ssubom.writing.entity.Reaction;
import swyp_11.ssubom.writing.entity.ReactionType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReactionTest {

    private User testUser;
    private Post testPost;
    private ReactionType likeType;
    private ReactionType loveType;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        testUser = mock(User.class);
        testPost = mock(Post.class);
        likeType = mock(ReactionType.class);
        loveType = mock(ReactionType.class);
    }

    @Test
    @DisplayName("Reaction 생성 성공")
    void create_success() {
        // when
        Reaction reaction = Reaction.create(testUser, testPost, likeType);

        // then
        assertNotNull(reaction);
        assertEquals(testUser, reaction.getUser());
        assertEquals(testPost, reaction.getPost());
        assertEquals(likeType, reaction.getType());
    }

    @Test
    @DisplayName("Reaction 생성 실패 - User가 null")
    void create_fail_user_null() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            Reaction.create(null, testPost, likeType);
        });
    }

    @Test
    @DisplayName("Reaction 생성 실패 - Post가 null")
    void create_fail_post_null() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            Reaction.create(testUser, null, likeType);
        });
    }

    @Test
    @DisplayName("Reaction 타입 변경 성공")
    void changeType_success() {
        // given
        Reaction reaction = Reaction.create(testUser, testPost, likeType);

        // when
        reaction.addType(loveType);

        // then
        assertEquals(loveType, reaction.getType());
    }

    @Test
    @DisplayName("Reaction 타입 변경 - 동일한 타입으로 변경 시도")
    void changeType_same_type() {
        // given
        Reaction reaction = Reaction.create(testUser, testPost, likeType);

        // when
        reaction.addType(likeType); // 같은 타입으로 변경 시도

        // then
        assertEquals(likeType, reaction.getType()); //
    }

    @Test
    @DisplayName("Reaction 타입 변경 - null로 변경 시도")
    void changeType_null() {
        // given
        Reaction reaction = Reaction.create(testUser, testPost, likeType);

        // when
        reaction.addType(null); // null로 변경 시도

        // then
        assertEquals(likeType, reaction.getType()); // 타입이 변경되지 않아야 함
    }
}