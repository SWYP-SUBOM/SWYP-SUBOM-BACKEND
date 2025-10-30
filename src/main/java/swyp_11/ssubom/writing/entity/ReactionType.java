package swyp_11.ssubom.writing.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ReactionType")
public class ReactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reaction_name")
    private String name;

}
