package swyp_11.ssubom.domain.topic.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp_11.ssubom.domain.common.BaseTimeEntity;

import java.time.LocalDate;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "topic_type", length = 20, nullable = false)
    private TopicType topicType;

    @Column(name = "embedding_json", columnDefinition = "TEXT")
    private String embeddingJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "topic_status")
    private Status topicStatus;

    @Transient
    private List<Double> embedding;

    public void use(LocalDate today) {
        this.isUsed = true;
        this.usedAt = today;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTopicStatus(Status newStatus){
        this.topicStatus=newStatus;
    }
    public void updateNameAndType(String topicName, TopicType topicType) {
        if (topicName != null) {
            this.name = topicName;
        }
        if (topicType != null) {
            this.topicType = topicType;
        }
    }

    public static Topic create(Category category, String topicName,TopicType topicType,List<Double> embedding) {
        Topic topic = new Topic();
        topic.category = category;
        topic.name = topicName;
        topic.topicType = topicType;
        topic.embedding = embedding;
        topic.topicStatus=Status.PENDING;
        topic.embeddingJson = toJson(embedding);
        topic.isUsed = false;
        topic.usedAt = null;
        return topic;
    }
    private static String toJson(List<Double> embedding) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(embedding);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Double> getEmbedding() {
        if (embedding == null && embeddingJson != null) {
            try {
                embedding = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(embeddingJson, new com.fasterxml.jackson.core.type.TypeReference<List<Double>>() {});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return embedding;
    }
}