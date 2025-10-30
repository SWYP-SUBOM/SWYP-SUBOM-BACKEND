package swyp_11.ssubom.global.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.global.security.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByKakaoId(String kakaoId);

}
