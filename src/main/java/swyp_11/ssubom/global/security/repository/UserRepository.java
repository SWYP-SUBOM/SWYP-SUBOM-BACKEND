package swyp_11.ssubom.global.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp_11.ssubom.global.security.dto.userDTO;
import swyp_11.ssubom.global.security.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByKakaoId(String kakaoId);

}
