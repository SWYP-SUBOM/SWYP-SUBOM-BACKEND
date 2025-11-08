package swyp_11.ssubom.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyp_11.ssubom.domain.post.dto.PostReactionInfo;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.entity.Reaction;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByPostAndUser(Post post, User user);

    @Query("SELECT r.type.name, COUNT(r) " +
            "FROM Reaction r " +
            "WHERE r.post = :post " +
            "GROUP BY r.type.name")
    List<Object[]> findReactionCountsByPost(@Param("post") Post post);

    @Query("""
        SELECT new swyp_11.ssubom.domain.post.dto.PostReactionInfo(
            r.type.id,
            r.type.name,
            COUNT(r)
        )
        FROM Reaction r
        WHERE r.post.id = :postId
        GROUP BY r.type.id, r.type.name
    """)
    List<PostReactionInfo> countReactionsByPostId(@Param("postId") Long postId);

    long countByPost(Post post);
}
