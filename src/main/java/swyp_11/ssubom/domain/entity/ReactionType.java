<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/entity/ReactionType.java
package swyp_11.ssubom.domain.entity;
========
package swyp_11.ssubom.domain.writing.entity;
>>>>>>>> develop:src/main/java/swyp_11/ssubom/domain/writing/entity/ReactionType.java

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "ReactionType")
public class ReactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reaction_name")
    private String name;

}
