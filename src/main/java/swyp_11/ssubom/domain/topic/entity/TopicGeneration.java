package swyp_11.ssubom.domain.topic.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class TopicGeneration {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TopicGenerationStatus status;
    // PROCESSING, COMPLETED, COMPLETED_WITH_ERRORS,FAILED

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    //생성 메서드
    public static TopicGeneration start(){
        TopicGeneration tg = new TopicGeneration();
        tg.status=TopicGenerationStatus.PROCESSING;
        tg.startedAt=LocalDateTime.now();
        return tg;
    }

    public void complete() {
        this.status = TopicGenerationStatus.COMPLETED;
        this.finishedAt = LocalDateTime.now();
    }

    public void completeWithErrors(String errorMessage) {
        this.status = TopicGenerationStatus.COMPLETED_WITH_ERRORS;
        this.errorMessage = errorMessage;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = TopicGenerationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.finishedAt = LocalDateTime.now();
    }
}
