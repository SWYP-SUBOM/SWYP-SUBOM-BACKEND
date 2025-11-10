package swyp_11.ssubom.domain.post.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.service.PostReadServiceImpl;
import swyp_11.ssubom.domain.post.service.ReactionService;
import swyp_11.ssubom.global.response.ApiResponse;
import swyp_11.ssubom.domain.post.service.PostService;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> createPost(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody PostCreateRequest request
    ) {
        Long userId = customOAuth2User.getUserId();

        PostCreateResponse postCreateResponse = postService.createPost(userId, request);
        ApiResponse<PostCreateResponse> responseBody = ApiResponse.success(
                postCreateResponse,
                "P0001",
                "글 임시저장에 성공했습니다"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> updatePost(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        Long userId = customOAuth2User.getUserId();

        PostUpdateResponse postUpdateResponse = postService.updatePost(
                userId,
                postId,
                request
        );

        ApiResponse<PostUpdateResponse> responseBody = ApiResponse.success(
                postUpdateResponse,
                "P0002",
                "임시저장한 글 수정에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long postId
    ) {
        Long userId = customOAuth2User.getUserId();
        postService.deletePost(userId, postId);

        ApiResponse<Void> responseBody = ApiResponse.success(
                null,
                "P0003",
                "글 삭제에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
    private final ReactionService reactionService;


    @PutMapping("/{postId}/reaction")
    public ResponseEntity<ApiResponse<ReactionResponse>> upsertReaction(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long postId,
            @Valid @RequestBody ReactionUpsertRequest request
    ) {
        Long userId = customOAuth2User.getUserId();

        ReactionResponse reactionResponse = reactionService.upsertReaction(userId, postId, request);

        ApiResponse<ReactionResponse> responseBody = ApiResponse.success(
                reactionResponse,
                "P0004",
                "반응 등록/수정에 성공했습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @DeleteMapping("/{postId}/reaction")
    public ResponseEntity<ApiResponse<ReactionResponse>> deleteReaction(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long postId
    ) {
        Long userId = customOAuth2User.getUserId();
        ReactionResponse reactionResponse = reactionService.deleteReaction(userId, postId);

        ApiResponse<ReactionResponse> responseBody = ApiResponse.success(
                reactionResponse,
                "R0002",
                "반응 삭제에 성공했습니다."
        );

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);

    }

    @Operation(
        summary = "글 상세 조회: 1) 피드용 2) 이어쓰기/마이페이지 내 글 상세 보기용",
        description = """
            1) 피드에서 글을 상세 조회합니다.
            2) 
            비로그인 사용자는 피드 상세 조회 불가.
        """,
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getPostDetail(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long postId,
            @RequestParam(name="context", required=false) String context
    ) {
        if ("edit".equals(context)) {
            MyPostDetailResponseDto myPostDetailResponse = postReadService.getMyPostDetail(user, postId);
            return ResponseEntity.ok(ApiResponse.success(myPostDetailResponse, "P0007", "내가 쓴 글 상세 조회에 성공했습니다."));
        } else {
            PostDetailResponse postDetailResponse = postService.getPostDetail(user, postId);
            return ResponseEntity.ok(ApiResponse.success(postDetailResponse, "F0001", "글 상세 조회에 성공했습니다."));
        }
    }

    private final PostReadServiceImpl postReadService;

    @GetMapping("/my-writings")
    public ResponseEntity<ApiResponse<MyPostResponseDto>> getMyWritings(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            MyPostRequestDto request
    ) {
        Long userId = customOAuth2User.getUserId();
        MyPostResponseDto myPostResponse = postReadService.getMyPosts(userId, request);
        return ResponseEntity.ok(ApiResponse.success(myPostResponse, "P0005", "내가 쓴 글 리스트 조회에 성공했습니다."));
    }

    @GetMapping("/my-reactions")
    public ResponseEntity<ApiResponse<MyReactedPostResponseDto>> getMyReactedPosts(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            MyReactedPostRequestDto request
    ) {
        Long userId = customOAuth2User.getUserId();
        MyReactedPostResponseDto myReactedPostResponse = postReadService.getMyReactedPost(userId, request);
        return ResponseEntity.ok(ApiResponse.success(myReactedPostResponse, "P0006", "내가 반응한 글 리스트 조회에 성공했습니다"));
    }

    @Operation(
            summary = "피드 전체 조회 ",
            description = "카테고리 별로 feed를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponseDto>> getPostListByCategoryId(
            @RequestParam(name = "categoryId",defaultValue = "1") Long categoryId) {
        PostListResponseDto responseDto = postService.getPostList(categoryId);
        return ResponseEntity.ok(ApiResponse.success(responseDto,"F0002","글 리스트 조회에 성공했습니다."));
    }
}
