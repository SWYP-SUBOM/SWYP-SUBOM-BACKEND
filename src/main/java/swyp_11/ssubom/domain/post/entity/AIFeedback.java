package swyp_11.ssubom.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "ai_feedback")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AIFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_feedback_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AIFeedbackStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String strength;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "improvement_point",
            joinColumns = @JoinColumn(name = "ai_feedback_id")
    )
    @Column(name = "improvement_points", columnDefinition = "TEXT")
    private List<String> improvementPoints = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length=2)
    private AIFeedbackGrade grade;

    public static AIFeedback createProcessingFeedback(Post post, String content) {
        AIFeedback feedback = new AIFeedback();
        feedback.post = post;
        feedback.content = content; // 원본 content 저장
        feedback.status = AIFeedbackStatus.PROCESSING;
        return feedback;
    }

    public void completeFeedback(String summary, String strength, List<String> points, String rawGrade) {
        this.summary = summary;
        this.strength = strength;
        this.improvementPoints = new ArrayList<>(points);
        this.status = AIFeedbackStatus.COMPLETED;
        this.grade = AIFeedbackGrade.fromString(rawGrade);
    }

    public void failFeedback(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = AIFeedbackStatus.FAILED;
    }
}