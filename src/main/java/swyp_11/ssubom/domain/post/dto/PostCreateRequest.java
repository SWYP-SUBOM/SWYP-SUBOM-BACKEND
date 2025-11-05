package swyp_11.ssubom.domain.post.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {
    @NotNull
    private Long categoryId;

    @NotNull
    private Long topicId;

    @NotBlank
    private String content;

}