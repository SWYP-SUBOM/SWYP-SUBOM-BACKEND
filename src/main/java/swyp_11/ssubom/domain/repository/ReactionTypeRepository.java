package swyp_11.ssubom.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_11.ssubom.domain.entity.ReactionType;

public interface ReactionTypeRepository extends JpaRepository<ReactionType, Long> {
    ReactionType findByName(String name);
}
