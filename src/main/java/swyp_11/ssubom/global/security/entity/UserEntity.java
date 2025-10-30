package swyp_11.ssubom.global.security.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long userId;
    private String kakaoId;
    public String userName;
    public String email;
    public String role;
    private LocalDateTime createdAt;

}
