package swyp_11.ssubom.domain.topic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.domain.topic.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
