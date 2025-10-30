//package swyp_11.ssubom.domain.writing.service;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import swyp_11.ssubom.domain.topic.entity.Topic;
//import swyp_11.ssubom.domain.writing.entity.AIFeedback;
//import swyp_11.ssubom.global.error.BusinessException;
//import swyp_11.ssubom.global.error.ErrorCode;
//import swyp_11.ssubom.domain.writing.entity.Post;
//import swyp_11.ssubom.domain.writing.dto.WritingCreateRequest;
//import swyp_11.ssubom.global.security.entity.User;
//import swyp_11.ssubom.global.security.repository.UserRepository;
//import swyp_11.ssubom.writing.dto.WritingCreateResponse;
//import swyp_11.ssubom.domain.writing.repository.PostRepository;
//
//@Service
//@Transactional
//@AllArgsConstructor
//public class WritingServiceImpl implements WritingService {
//    private final PostRepository postRepository;
//    private final UserRepository userRepository;
//    private final TopicRepository topicRepository;
//    private final AIFeedbackRepository aiFeedbackRepository;
//    private final StreakRepository streakRepository;
//
//    @Override
//    public WritingCreateResponse createWriting(Long userId, WritingCreateRequest request) {
//        // 1. validation
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//        Topic topic = topicRepository.findById(request.getTopicId())
//                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
//        AIFeedback feedBack = null;
//        if (request.getFeedbackId() != null) {
//            feedBack = aiFeedbackRepository.findById(request.getFeedbackId())
//                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
//        }
//
//        // 2. dto -> entity mapping
//        Post newPost = Post.builder()
//                .user(user)
//                .topic(topic)
//                .content(request.getContent())
//                .status(request.getStatus())
//
//                .build();
//
//    }
//
//
//}
