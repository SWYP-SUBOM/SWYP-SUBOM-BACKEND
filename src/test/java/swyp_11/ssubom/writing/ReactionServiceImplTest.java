package swyp_11.ssubom.writing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.user.entity.User;
import swyp_11.ssubom.user.repository.UserRepository;
import swyp_11.ssubom.writing.dto.ReactionMetricsDto;
import swyp_11.ssubom.writing.dto.ReactionResponse;
import swyp_11.ssubom.writing.dto.ReactionUpsertRequest;
import swyp_11.ssubom.writing.entity.Post;
import swyp_11.ssubom.writing.entity.Reaction;
import swyp_11.ssubom.writing.entity.ReactionType;
import swyp_11.ssubom.writing.repository.PostRepository;
import swyp_11.ssubom.writing.repository.ReactionRepository;
import swyp_11.ssubom.writing.repository.ReactionTypeRepository;
import swyp_11.ssubom.writing.service.ReactionServiceImpl;

import java.util.Arrays; // Import Arrays
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReactionService 로직에 대한 단위 테스트
 * Repository 의존성을 @Mock으로 대체하여 서비스 로직만 격리 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
class ReactionServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReactionTypeRepository reactionTypeRepository;
    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionServiceImpl reactionService;

    // 테스트용 공통 객체
    private User testUser;
    private Post testPost;
    private ReactionType likeType;
    private ReactionType loveType;
    private ReactionUpsertRequest likeRequest;
    private ReactionUpsertRequest loveRequest;
    private final Long TEST_USER_ID = 1L;
    private final Long TEST_POST_ID = 1L;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        testUser = mock(User.class);
        testPost = mock(Post.class);
        likeType = mock(ReactionType.class);
        loveType = mock(ReactionType.class);

        // DTO 객체 생성
        likeRequest = new ReactionUpsertRequest("LIKE");
        loveRequest = new ReactionUpsertRequest("LOVE");

        // Mock 객체의 메서드 호출 시 반환값 정의

        // FIX: Wrap ALL stubs in setUp with lenient()
        lenient().when(testPost.getPostId()).thenReturn(TEST_POST_ID);
        lenient().when(likeType.getName()).thenReturn("LIKE");
        lenient().when(loveType.getName()).thenReturn("LOVE");
    }

    // --- upsertReaction 테스트 ---

    @Test
    @DisplayName("새로운 반응 생성 성공 (첫 반응)")
    void upsertReaction_CreateNew() {
        // Arrange (Given)
        // 1. Repository Mocking 설정
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        when(reactionTypeRepository.findByName("LIKE")).thenReturn(likeType);

        // 2. 이전에 반응이 없었음을 설정 (Optional.empty() 반환)
        when(reactionRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());

        // 3. 반응 집계(calculateReactionMetrics) Mocking 설정
        //    새로운 반응이 추가된 후의 상태를 시뮬레이션
        // FIX 1: List.of() -> Collections.singletonList()
        List<Object[]> counts = Collections.singletonList(new Object[]{"LIKE", 1L});
        when(reactionRepository.findReactionCountsByPost(testPost)).thenReturn(counts);

        // Act (When)
        ReactionResponse response = reactionService.upsertReaction(TEST_USER_ID, TEST_POST_ID, likeRequest);

        // Assert (Then)
        assertNotNull(response);
        assertEquals(TEST_POST_ID, response.getPostId());
        assertEquals("LIKE", response.getCurrentUserReaction()); // 현재 유저 반응 = "LIKE"

        ReactionMetricsDto metrics = response.getMetrics();
        // FIX 2: getTotalReactionCounts() -> getTotalReaction()
        assertEquals(1L, metrics.getTotalReactions()); // 전체 반응 수 = 1
        assertEquals(1L, metrics.getCountsByType().get("LIKE")); // "LIKE" 반응 수 = 1

        // 4. reactionRepository.save()가 1번 호출되었는지 검증 (새로운 Reaction이 저장됨)
        verify(reactionRepository, times(1)).save(any(Reaction.class));
    }

    @Test
    @DisplayName("기존 반응 수정 성공 (LIKE -> LOVE)")
    void upsertReaction_UpdateExisting() {
        // Arrange (Given)
        // 1. 기존 'LIKE' 반응이 존재하는 상황을 만들기 위해 'spy' 사용
        Reaction existingReaction = spy(Reaction.create(testUser, testPost, likeType));

        // 2. Repository Mocking 설정
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        when(reactionTypeRepository.findByName("LOVE")).thenReturn(loveType);

        // 3. 이전에 'LIKE' 반응이 있었음을 설정
        when(reactionRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingReaction));

        // 4. 반응 집계(calculateReactionMetrics) Mocking 설정 (LOVE로 변경된 상태)
        // FIX 1: List.of() -> Collections.singletonList()
        List<Object[]> counts = Collections.singletonList(new Object[]{"LOVE", 1L});
        when(reactionRepository.findReactionCountsByPost(testPost)).thenReturn(counts);

        // Act (When)
        ReactionResponse response = reactionService.upsertReaction(TEST_USER_ID, TEST_POST_ID, loveRequest);

        // Assert (Then)
        assertNotNull(response);
        assertEquals(TEST_POST_ID, response.getPostId());
        assertEquals("LOVE", response.getCurrentUserReaction()); // 현재 유저 반응 = "LOVE"

        ReactionMetricsDto metrics = response.getMetrics();
        // FIX 2: getTotalReactionCounts() -> getTotalReaction()
        assertEquals(1L, metrics.getTotalReactions()); // 전체 반응 수 = 1
        assertEquals(1L, metrics.getCountsByType().get("LOVE")); // "LOVE" 반응 수 = 1

        // 5. existingReaction.addType(loveType)이 호출되었는지 검증
        verify(existingReaction, times(1)).addType(loveType);

        // 6. reactionRepository.save()가 호출되지 않았는지 검증 (JPA Dirty Checking으로 자동 업데이트됨)
        verify(reactionRepository, never()).save(any(Reaction.class));
    }

    @Test
    @DisplayName("Upsert - 유저를 찾을 수 없음")
    void upsertReaction_UserNotFound() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reactionService.upsertReaction(TEST_USER_ID, TEST_POST_ID, likeRequest);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Upsert - 포스트를 찾을 수 없음")
    void upsertReaction_PostNotFound() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reactionService.upsertReaction(TEST_USER_ID, TEST_POST_ID, likeRequest);
        });

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Upsert - 유효하지 않은 반응 타입")
    void upsertReaction_InvalidReactionType() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        when(reactionTypeRepository.findByName("INVALID_TYPE")).thenReturn(null); // DB에 없는 타입

        ReactionUpsertRequest invalidRequest = new ReactionUpsertRequest("INVALID_TYPE");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reactionService.upsertReaction(TEST_USER_ID, TEST_POST_ID, invalidRequest);
        });

        assertEquals(ErrorCode.INVALID_REACTION_TYPE, exception.getErrorCode());
    }

    // --- deleteReaction 테스트 ---

    @Test
    @DisplayName("반응 삭제 성공")
    void deleteReaction_Success() {
        // Arrange (Given)
        // 1. 기존 'LIKE' 반응이 존재하는 상황 설정
        Reaction existingReaction = Reaction.create(testUser, testPost, likeType);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        when(reactionRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingReaction));

        // 2. 반응 집계(calculateReactionMetrics) Mocking 설정 (반응이 0개가 된 상태)
        // FIX 1: List.of() -> Collections.emptyList()
        when(reactionRepository.findReactionCountsByPost(testPost)).thenReturn(Collections.emptyList());

        // Act (When)
        ReactionResponse response = reactionService.deleteReaction(TEST_USER_ID, TEST_POST_ID);

        // Assert (Then)
        assertNotNull(response);
        assertEquals(TEST_POST_ID, response.getPostId());
        assertNull(response.getCurrentUserReaction()); // 현재 유저 반응 = null

        ReactionMetricsDto metrics = response.getMetrics();
        // FIX 2: getTotalReactionCounts() -> getTotalReaction()
        assertEquals(0L, metrics.getTotalReactions()); // 전체 반응 수 = 0
        assertTrue(metrics.getCountsByType().isEmpty()); // 타입별 집계 맵이 비어있음

        // 3. reactionRepository.delete()가 1번 호출되었는지 검증
        verify(reactionRepository, times(1)).delete(existingReaction);
    }

    @Test
    @DisplayName("반응 삭제 - 삭제할 반응을 찾을 수 없음")
    void deleteReaction_ReactionNotFound() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        // 1. 삭제할 반응이 없음 (Optional.empty() 반환)
        when(reactionRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reactionService.deleteReaction(TEST_USER_ID, TEST_POST_ID);
        });

        assertEquals(ErrorCode.REACTION_NOT_FOUND, exception.getErrorCode());
    }

    // --- calculateReactionMetrics (private) 테스트 ---

    @Test
    @DisplayName("반응 집계 로직 테스트 (여러 타입)")
    void calculateReactionMetrics_MultipleTypes() {
        // 이 테스트는 deleteReaction의 응답을 통해 집계 로직을 검증합니다.

        // Arrange
        Reaction existingReaction = Reaction.create(testUser, testPost, likeType);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        when(reactionRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingReaction));

        // 1. 삭제 *전* 집계 로직이 호출될 때 (deleteReaction 내부)
        //    "LIKE" 3개, "LOVE" 2개가 있었다고 가정
        // FIX 1: List.of() -> Arrays.asList()
        List<Object[]> counts = Arrays.asList(
                new Object[]{"LIKE", 3L},
                new Object[]{"LOVE", 2L}
        );
        when(reactionRepository.findReactionCountsByPost(testPost)).thenReturn(counts);

        // Act
        ReactionResponse response = reactionService.deleteReaction(TEST_USER_ID, TEST_POST_ID);

        // Assert
        // deleteReaction은 삭제 후의 집계를 반환합니다.
        ReactionMetricsDto metrics = response.getMetrics();
        // FIX 2: getTotalReactionCounts() -> getTotalReaction()
        assertEquals(5L, metrics.getTotalReactions()); // 3 + 2 = 5
        assertEquals(3L, metrics.getCountsByType().get("LIKE"));
        assertEquals(2L, metrics.getCountsByType().get("LOVE"));
    }
}