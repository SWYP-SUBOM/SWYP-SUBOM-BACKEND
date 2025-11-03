<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/entity/ImprovementPoint.java
package swyp_11.ssubom.domain.entity;
========
package swyp_11.ssubom.domain.writing.entity;
>>>>>>>> develop:src/main/java/swyp_11/ssubom/domain/writing/entity/ImprovementPoint.java

import jakarta.persistence.*;

@Entity
@Table(name = "ImprovementPoint")
public class ImprovementPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "improvement_point_id")
    private Long id;

    @Lob
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_feedback_id", nullable = false)
    private AIFeedback aiFeedback;
}
