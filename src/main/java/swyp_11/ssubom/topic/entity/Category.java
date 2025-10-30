package swyp_11.ssubom.topic.entity;

import jakarta.persistence.*;

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", length = 20)
    private String name;
}
