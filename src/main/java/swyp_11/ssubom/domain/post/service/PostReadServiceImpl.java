package swyp_11.ssubom.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp_11.ssubom.domain.post.dto.*;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.repository.PostRepository;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostReadServiceImpl implements PostReadService {
    private final PostRepository postRepository;

    @Override
    public MyPostResponseDto getMyPosts(Long userId, MyPostRequestDto request) {
        Pageable pageable = createPageable(request);
        Page<Post> postPage = postRepository.findMyPosts(userId, request, pageable);
        List<MyPostItem> items = postPage.getContent().stream()
                .map(this::convertPostToMyPostItem)
                .toList();

        PageInfoDto pageInfo = new PageInfoDto(
                postPage.getNumber() + 1,
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.isLast()
        );
        return new MyPostResponseDto(items, pageInfo);
    }

    @Override
    public MyReactedPostResponseDto getMyReactedPost(Long userId, MyReactedPostRequestDto request) {
        // TODO: 구현 필요
        return null;
    }

    @Override
    public MyPostDetailResponseDto getPostDetail(Long postId) {
        // TODO: 구현 필요
        return null;
    }

    private Pageable createPageable(MyPostRequestDto request) {
        int page = request.getPage() - 1;
        int size = request.getSize();
        Sort sort;
        if (request.getSort().equals("oldest")) {
            sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        return PageRequest.of(page, size, sort);
    }

    private MyPostItem convertPostToMyPostItem(Post post) {
        Topic topic = post.getTopic();
        Category category = topic.getCategory();
        TopicInfo topicInfo = new TopicInfo(topic.getName(), category.getName());
        AIFeedback aiFeedback = post.getAiFeedback();

        return new MyPostItem(
                post.getPostId(),
                topicInfo,
                aiFeedback.getSummary(),
                post.getStatus().toString(),
                post.isRevised(),
                post.getUpdatedAt()
        );
    }
}
