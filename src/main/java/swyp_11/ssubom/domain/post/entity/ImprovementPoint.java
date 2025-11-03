package swyp_11.ssubom.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

@Getter
@Entity
@Table(name = "improvement_point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImprovementPoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "improvement_point_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_feedback_id", nullable = false)
    private AIFeedback aiFeedback;
}
