package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import swyp_11.ssubom.domain.post.entity.AIFeedback;
import swyp_11.ssubom.domain.post.entity.Post;
import swyp_11.ssubom.domain.post.dto.ImprovementPointDto;
import java.util.List;
import java.util.stream.Collectors;
@Getter
public class AiFeedbackResponse {
    private Long postId;
    private Long aiFeedbackId;
    private String strengthPoint;
    private String status;
    private List<ImprovementPointDto> improvementPoints;
    private String summary;

    @Builder
    public AiFeedbackResponse(Post post, AIFeedback aiFeedback){
        this.postId = post.getPostId();
        this.aiFeedbackId = aiFeedback.getId();
        this.strengthPoint= aiFeedback.getStrength();
        this.summary = aiFeedback.getSummary();
        this.status = aiFeedback.getStatus().name();
        this.improvementPoints =aiFeedback.getImprovementPoints()
                .stream()
                .map(ip->new ImprovementPointDto(ip.getId(),ip.getContent()))
                .collect(Collectors.toList());

    }

}
