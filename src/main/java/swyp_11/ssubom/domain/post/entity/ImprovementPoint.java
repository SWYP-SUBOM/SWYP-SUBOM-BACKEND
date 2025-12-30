package swyp_11.ssubom.domain.post.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImprovementPoint {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;  // 피드백 내용

    @Column(name = "sentence_index", nullable = false)
    private int sentenceIndex; // 문장 번호 (없으면 -1)
}
