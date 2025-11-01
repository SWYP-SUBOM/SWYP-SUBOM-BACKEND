package swyp_11.ssubom.topic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_11.ssubom.topic.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
