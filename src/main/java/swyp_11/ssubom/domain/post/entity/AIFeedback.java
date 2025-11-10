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

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String strength;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AiStatus status; //DRAFT, PUBLISHED

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @OneToMany(mappedBy = "aiFeedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImprovementPoint> improvementPoints = new ArrayList<>();
}