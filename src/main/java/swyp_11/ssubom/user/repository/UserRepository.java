package swyp_11.ssubom.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_11.ssubom.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
