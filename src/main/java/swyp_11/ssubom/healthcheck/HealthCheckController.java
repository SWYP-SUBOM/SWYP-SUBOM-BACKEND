package swyp_11.ssubom.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @Autowired
    private HealthCheckRepository healthCheckRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. DB에 데이터 저장
            HealthCheck healthCheck = HealthCheck.builder()
                    .message("health check test")
                    .checkedAt(LocalDateTime.now())
                    .build();
            HealthCheck saved = healthCheckRepository.save(healthCheck);

            // 2. 저장된 데이터 조회
            HealthCheck retrieved = healthCheckRepository.findById(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Data not found"));

            // 3. 응답 생성
            response.put("status", "UP");
            response.put("database", "CONNECTED");
            response.put("saved", Map.of(
                    "id", saved.getId(),
                    "message", saved.getMessage(),
                    "checkedAt", saved.getCheckedAt()
            ));
            response.put("retrieved", Map.of(
                    "id", retrieved.getId(),
                    "message", retrieved.getMessage(),
                    "checkedAt", retrieved.getCheckedAt()
            ));
            response.put("testResult", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("database", "ERROR");
            response.put("error", e.getMessage());
            response.put("testResult", "FAILED");
            return ResponseEntity.status(503).body(response);
        }
    }
}
