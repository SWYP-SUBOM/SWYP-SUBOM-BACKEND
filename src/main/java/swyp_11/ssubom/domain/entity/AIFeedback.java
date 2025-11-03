<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/entity/AIFeedback.java
package swyp_11.ssubom.domain.entity;
========
package swyp_11.ssubom.domain.writing.entity;
>>>>>>>> develop:src/main/java/swyp_11/ssubom/domain/writing/entity/AIFeedback.java

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "AIFeedback")
public class AIFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_feedback_id")
    private Long id;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "strength")
    private String strength;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;


}