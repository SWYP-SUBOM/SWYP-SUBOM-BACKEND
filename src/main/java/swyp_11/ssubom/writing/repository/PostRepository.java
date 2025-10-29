package swyp_11.ssubom.writing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.writing.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
