package swyp_11.ssubom.domain.topic.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyp_11.ssubom.domain.topic.entity.Status;
import swyp_11.ssubom.domain.topic.entity.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    //중복확인

    @Query("SELECT t FROM Topic t JOIN FETCH t.category " +
            "WHERE t.category.id = :categoryId AND t.isUsed = true AND t.usedAt = :usedAt")
    Optional<Topic> findByCategory_IdAndIsUsedTrueAndUsedAt(@Param("categoryId") Long categoryId, @Param("usedAt") LocalDate usedAt);

    //관리자 페이지 ) 이미 할당 완료한 경우
    @Query("SELECT t FROM Topic t " +
            "WHERE t.category.id = :categoryId " +
            "AND t.usedAt = :usedAt " +
            "AND t.topicStatus = 'APPROVED'")
    Optional<Topic> findReservedTopic(@Param("categoryId") Long categoryId , @Param("usedAt")LocalDate usedAt);

    //예약된 게 없을 때 사용 안된 것 하나 선택하기
    @Query(value = """
        select * from seobom.topic
        where category_id = :categoryId
        and is_used = false 
        and topic_status='APPROVED'
          ORDER BY random()
         FOR UPDATE SKIP LOCKED
        LIMIT 1
  """, nativeQuery = true)
    Topic lockOneUnused(@Param("categoryId") Long categoryId);

    List<Topic> findTop30ByCategoryIdAndIsUsedTrueOrderByUsedAtDesc(Long categoryId);

    List<Topic> findTop30ByCategoryIdAndIsUsedTrueOrderByUsedAtAsc(Long categoryId);

    Optional<Topic> findByUsedAtAndCategory_Id(LocalDate usedAt, Long categoryId);

    List<Topic> findTop40ByCategoryIdAndTopicStatusOrderByUpdatedAtDesc(Long categoryId, Status topicStatus);

    @Query("""
        select t from Topic t
                where (:categoryId is null or t.category.id=:categoryId )
                        and t.isUsed=false
                        and(:mode='ALL'
                                or(:mode='APPROVED' and t.topicStatus='APPROVED')
                                or(:mode = 'PENDING' AND t.topicStatus = 'PENDING')
                                or(:mode = 'QUESTION' AND t.topicType = 'QUESTION')
                                or(:mode = 'LOGICAL' AND t.topicType = 'LOGICAL')
                                        )
                        order by t.id desc 
        """)
    List<Topic> findAdminTopics(@Param("mode")String mode , @Param("categoryId") Long categoryId);


}
