package swyp_11.ssubom.domain.topic.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "topic")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @Column(name = "topic_name", length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_used", nullable = false, columnDefinition = "boolean default false")
    private boolean isUsed;

   @Column(name = "used_at")
    private LocalDate usedAt;

    public void use(LocalDate today) {
        this.isUsed = true;
        this.usedAt = today;
    }
}