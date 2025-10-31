package swyp_11.ssubom.writing.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WritingCreateRequest {
    @NotNull
    private Long categoryId;

    @NotNull
    private Long topicId;

    @NotBlank
    private String content;

    @NotBlank
    private String status; // "DRAFT" is only possible.

    private Long aiFeedbackId;

}