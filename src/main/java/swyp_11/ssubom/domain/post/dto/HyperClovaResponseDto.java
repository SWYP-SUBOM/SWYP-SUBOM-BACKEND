package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HyperClovaResponseDto {
    private String summary;
    private String strength;
    private List<String> improvementPoints;
    private String grade;
}
