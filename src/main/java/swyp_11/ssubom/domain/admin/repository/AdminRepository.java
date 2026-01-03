package swyp_11.ssubom.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp_11.ssubom.domain.admin.entity.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByEmail(String email);
}
