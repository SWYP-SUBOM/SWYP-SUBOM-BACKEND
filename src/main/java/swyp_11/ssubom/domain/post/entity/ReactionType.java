package swyp_11.ssubom.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reaction_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reaction_name")
    private String name;
}
