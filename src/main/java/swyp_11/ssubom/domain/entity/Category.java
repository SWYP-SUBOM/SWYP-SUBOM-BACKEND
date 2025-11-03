<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/entity/Category.java
package swyp_11.ssubom.domain.entity;
========
package swyp_11.ssubom.domain.topic.entity;
>>>>>>>> develop:src/main/java/swyp_11/ssubom/domain/topic/entity/Category.java

import jakarta.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", length = 20)
    private String name;
}
