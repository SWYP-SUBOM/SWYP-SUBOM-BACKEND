package swyp_11.ssubom.writing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.topic.entity.Topic;
import swyp_11.ssubom.topic.repository.TopicRepository;
import swyp_11.ssubom.user.entity.User;
import swyp_11.ssubom.user.repository.UserRepository;
import swyp_11.ssubom.writing.dto.WritingCreateRequest;
import swyp_11.ssubom.writing.dto.WritingCreateResponse;
// ... (WritingUpdateRequest import는 delete 테스트에서 사용 안 하므로 유지)
import swyp_11.ssubom.writing.entity.Post;
import swyp_11.ssubom.writing.entity.PostStatus;
import swyp_11.ssubom.writing.repository.PostRepository;
import swyp_11.ssubom.writing.service.WritingServiceImpl;
import swyp_11.ssubom.writing.service.nickname.NicknameGenerator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 2순위 테스트 (서비스 Mockito 테스트)
 * - @ExtendWith(MockitoExtension.class)로 Mockito 환경을 사용합니다.
 * - @Mock: Service의 모든 의존성(Repository, Component)을 가짜로 만듭니다.
 * - @InjectMocks: 가짜 객체들을 Service에 주입합니다.
 * - DB에 전혀 접근하지 않습니다.
 */
@ExtendWith(MockitoExtension.class)
class WritingServiceImplTest {

    // 2순위 전략: Service의 모든 의존성을 @Mock으로 가짜로 만듭니다.
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NicknameGenerator nicknameGenerator;
    @Mock
    private TopicRepository topicRepository;

    // @InjectMocks: 위에서 @Mock으로 만든 가짜 객체들을 'writingService'에 주입합니다.
    @InjectMocks
    private WritingServiceImpl writingService;

    @Test
    @DisplayName("createWriting: 성공 (닉네임 중복 없음, 1회 시도)")
    void createWriting_Success_OnFirstTry() {
        // given
        Long userId = 1L;
        Long categoryId = 5L; // DTO 변경으로 추가
        Long topicId = 10L;
        String content = "테스트 글 내용";
        String status = "DRAFT"; // DTO 변경으로 추가

        // DTO 생성자 변경: 4개 인자를 받도록 수정
        WritingCreateRequest request = new WritingCreateRequest(categoryId, topicId, content, status);

        // 의존성 Mock 객체 생성
        User mockUser = mock(User.class);
        Topic mockTopic = mock(Topic.class);

        // Post.create()는 실제 엔티티 객체를 반환해야 함
        Post savedPost = Post.create(mockUser, mockTopic, content, PostStatus.DRAFT, "멋진-닉네임");

        // Mockito 행동 정의 (Given)
        // 1. userRepository.findById(userId)가 호출되면 -> mockUser를 반환
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // 2. topicRepository.findById(topicId)가 호출되면 -> mockTopic을 반환
        given(topicRepository.findById(topicId)).willReturn(Optional.of(mockTopic));

        // 3. nicknameGenerator.generateNickname(userId)가 호출되면 -> "멋진-닉네임"을 반환
        given(nicknameGenerator.generateNickname(userId)).willReturn("멋진-닉네임");

        // 4. postRepository.saveAndFlush(어떤 Post 객체든)가 호출되면 -> savedPost를 반환
        given(postRepository.saveAndFlush(any(Post.class))).willReturn(savedPost);

        // when
        // 실제 서비스 로직 호출
        WritingCreateResponse response = writingService.createWriting(userId, request);

        // then
        // 1. 결과 DTO 검증
        assertThat(response).isNotNull();
        // (WritingCreateResponse.of()가 잘 동작했다면, savedPost의 정보를 담고 있을 것)

        // 2. 요청하신 "호출" 검증 (상호작용 검증)

        // postRepository의 saveAndFlush가 '1번' 호출되었는지 검증
        verify(postRepository, times(1)).saveAndFlush(any(Post.class));

        // nicknameGenerator의 generateNickname이 '1번' 호출되었는지 검증
        verify(nicknameGenerator, times(1)).generateNickname(userId);

        // 3. (심화) postRepository.saveAndFlush()에 '의도한' Post 객체가 전달되었는지 검증
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).saveAndFlush(postCaptor.capture()); // 전달된 Post 객체 캡처

        Post capturedPost = postCaptor.getValue();
        assertThat(capturedPost.getUser()).isEqualTo(mockUser);
        assertThat(capturedPost.getTopic()).isEqualTo(mockTopic);
        assertThat(capturedPost.getContent()).isEqualTo(content);
        assertThat(capturedPost.getNickname()).isEqualTo("멋진-닉네임");
        assertThat(capturedPost.getStatus()).isEqualTo(PostStatus.DRAFT);
    }

    @Test
    @DisplayName("createWriting: 닉네임 중복 1회 후 재시도 성공")
    void createWriting_Success_OnRetry() {
        // given
        Long userId = 1L;
        Long categoryId = 5L; // DTO 변경으로 추가
        Long topicId = 10L;
        String content = "테스트 글 내용";
        String status = "DRAFT"; // DTO 변경으로 추가
        String conflictNickname = "중복-닉네임";
        String successNickname = "성공-닉네임";

        // DTO 생성자 변경: 4개 인자를 받도록 수정
        WritingCreateRequest request = new WritingCreateRequest(categoryId, topicId, content, status);

        User mockUser = mock(User.class);
        Topic mockTopic = mock(Topic.class);
        Post savedPost = Post.create(mockUser, mockTopic, content, PostStatus.DRAFT, successNickname);

        // Mockito 행동 정의 (Given)
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(topicRepository.findById(topicId)).willReturn(Optional.of(mockTopic));

        // 1. 닉네임 생성기: '처음'엔 "중복-닉네임"을, '그다음'엔 "성공-닉네임"을 반환
        given(nicknameGenerator.generateNickname(userId))
                .willReturn(conflictNickname)
                .willReturn(successNickname);

        // 2. 저장소: '처음'엔 DataIntegrityViolationException(중복 예외)을 던지고,
        //           '그다음'엔 savedPost를 반환
        given(postRepository.saveAndFlush(any(Post.class)))
                .willThrow(new DataIntegrityViolationException("Nickname conflict"))
                .willReturn(savedPost);

        // when
        WritingCreateResponse response = writingService.createWriting(userId, request);

        // then
        assertThat(response).isNotNull();

        // 1. 닉네임 생성기가 '2번' 호출되었는지 검증
        verify(nicknameGenerator, times(2)).generateNickname(userId);

        // 2. saveAndFlush가 '2번' 호출되었는지 검증 (중복 1번, 성공 1번)
        verify(postRepository, times(2)).saveAndFlush(any(Post.class));
    }

    @Test
    @DisplayName("createWriting: 닉네임 생성 5회(최대) 모두 실패")
    void createWriting_Fail_AfterMaxRetries() {
        // given
        Long userId = 1L;
        Long categoryId = 5L; // DTO 변경으로 추가
        Long topicId = 10L;
        String status = "DRAFT"; // DTO 변경으로 추가

        // DTO 생성자 변경: 4개 인자를 받도록 수정
        WritingCreateRequest request = new WritingCreateRequest(categoryId, topicId, "test", status);

        User mockUser = mock(User.class);
        Topic mockTopic = mock(Topic.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(topicRepository.findById(topicId)).willReturn(Optional.of(mockTopic));

        // 닉네임 생성은 항상 "중복-닉네임"을 반환
        given(nicknameGenerator.generateNickname(userId)).willReturn("중복-닉네임");

        // saveAndFlush는 '항상' 예외를 던짐
        given(postRepository.saveAndFlush(any(Post.class)))
                .willThrow(new DataIntegrityViolationException("Nickname conflict"));

        // when & then
        // 5번(SERVICE_LEVEL_MAX_TRIES) 시도 후 BusinessException이 터지는지 검증
        assertThatThrownBy(() -> writingService.createWriting(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NICKNAME_GENERATION_FAILED.getErrorMessage());

        // 닉네임 생성과 저장이 정확히 5번씩 시도되었는지 검증
        verify(nicknameGenerator, times(5)).generateNickname(userId);
        verify(postRepository, times(5)).saveAndFlush(any(Post.class));
    }

    @Test
    @DisplayName("deleteWriting: 성공 (본인 DRAFT 글 삭제)")
    void deleteWriting_Success_WhenOwnerDeletesDraft() {
        // ... (이하 delete 테스트는 DTO 변경과 무관하므로 수정 없음)
        // given
        Long userId = 1L;
        Long postId = 100L;

        // Mock 객체 및 행동 정의
        User mockUser = mock(User.class);
        Topic mockTopic = mock(Topic.class);
        given(mockUser.getId()).willReturn(userId); // post.getUser().getId()가 1L을 반환하도록 설정

        // 삭제 대상 Post (DRAFT 상태)
        Post draftPost = Post.create(mockUser, mockTopic, "초안", PostStatus.DRAFT, "nick-draft");

        // postRepository.findById()는 draftPost를 반환
        given(postRepository.findById(postId)).willReturn(Optional.of(draftPost));

        // when
        writingService.deleteWriting(userId, postId);

        // then
        // 1. 권한 검사 통과 (예외 없음)
        // 2. 상태 검사 통과 (예외 없음)
        // 3. postRepository.delete()가 '정확히' draftPost 객체를 대상으로 1번 호출되었는지 검증
        verify(postRepository, times(1)).delete(draftPost);
    }

    @Test
    @DisplayName("deleteWriting: 실패 (타인의 글 삭제 시도)")
    void deleteWriting_Fail_WhenUserIsNotOwner() {
        // ... (이하 delete 테스트는 DTO 변경과 무관하므로 수정 없음)
        // given
        Long myUserId = 1L; // 삭제를 시도하는 사용자
        Long ownerUserId = 2L; // 글의 실제 주인
        Long postId = 101L;

        User mockOwner = mock(User.class);
        Topic mockTopic = mock(Topic.class);
        given(mockOwner.getId()).willReturn(ownerUserId); // post.getUser().getId()가 2L을 반환

        Post othersPost = Post.create(mockOwner, mockTopic, "남의 글", PostStatus.DRAFT, "nick-other");

        given(postRepository.findById(postId)).willReturn(Optional.of(othersPost));

        // when & then
        // '1L' 사용자가 삭제를 시도
        assertThatThrownBy(() -> writingService.deleteWriting(myUserId, postId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.FORBIDDEN_WRITING_MODIFICATION.getErrorMessage());

        // delete 메서드가 '절대' 호출되지 않았는지 검증
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("deleteWriting: 실패 (PUBLISHED 상태 글 삭제 시도)")
    void deleteWriting_Fail_WhenPostIsPublished() {
        // ... (이하 delete 테스트는 DTO 변경과 무관하므로 수정 없음)
        // given
        Long userId = 1L; // 본인 글 맞음
        Long postId = 102L;

        User mockUser = mock(User.class);
        Topic mockTopic = mock(Topic.class);
        given(mockUser.getId()).willReturn(userId); // post.getUser().getId()가 1L을 반환

        // 삭제 대상 Post (PUBLISHED 상태)
        Post publishedPost = Post.create(mockUser, mockTopic, "발행된 글", PostStatus.PUBLISHED, "nick-pub");

        given(postRepository.findById(postId)).willReturn(Optional.of(publishedPost));

        // when & then
        assertThatThrownBy(() -> writingService.deleteWriting(userId, postId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.CANNOT_DELETE_PUBLISHED_POST.getErrorMessage());

        // delete 메서드가 '절대' 호출되지 않았는지 검증
        verify(postRepository, never()).delete(any(Post.class));
    }
}

