package swyp_11.ssubom.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByNickname(String nickname);
}
