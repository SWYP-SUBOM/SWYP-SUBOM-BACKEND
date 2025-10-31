package swyp_11.ssubom.domain.topic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyp_11.ssubom.domain.topic.entity.Category;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDate;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    //중복확인
    Optional<Topic> findByCategory_IdAndUsedTrueAndUsedAt(Long categoryId, LocalDate usedAt);

    //사용 안된 것 하나 선택하기
    @Query(value = """
        select * from topic
        where category_id = :categoryId
        and is_used = false 
          ORDER BY topic_id ASC
         FOR UPDATE SKIP LOCKED
        LIMIT 1
  """, nativeQuery = true)
    Topic lockOneUnused(@Param("categoryId") Long categoryId);

}
