<<<<<<<< HEAD:src/main/java/swyp_11/ssubom/domain/entity/Topic.java
package swyp_11.ssubom.domain.entity;
========
package swyp_11.ssubom.domain.topic.entity;
>>>>>>>> develop:src/main/java/swyp_11/ssubom/domain/topic/entity/Topic.java

import jakarta.persistence.*;

@Entity
@Table(name = "Topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @Column(name = "topic_name", length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}