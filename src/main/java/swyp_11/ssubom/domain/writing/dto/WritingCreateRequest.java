package swyp_11.ssubom.domain.writing.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WritingCreateRequest {
    @NotNull
    private Long categoryId;

    @NotNull
    private Long topicId;

    @NotBlank
    private String content;

    @NotBlank
    private String status; // "DRAFT" is only possible.

}