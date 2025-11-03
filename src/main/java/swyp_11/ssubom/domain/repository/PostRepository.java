<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/repository/PostRepository.java
package swyp_11.ssubom.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.entity.Post;
========
package swyp_11.ssubom.domain.writing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.writing.entity.Post;
>>>>>>>> develop:src/main/java/swyp_11/ssubom/domain/writing/repository/PostRepository.java

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByNickname(String nickname);
}
