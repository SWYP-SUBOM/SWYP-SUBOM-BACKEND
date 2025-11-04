package swyp_11.ssubom.global.nickname;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.post.repository.PostRepository;

@Component
public class NicknamePersistenceHelper {

    private final PostRepository postRepository;

    @Autowired
    public NicknamePersistenceHelper(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean exists(String nickname) {
        return postRepository.existsByNickname(nickname);
    }

}
