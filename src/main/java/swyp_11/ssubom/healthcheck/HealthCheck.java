package swyp_11.ssubom.healthcheck;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_check")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime checkedAt;
}
