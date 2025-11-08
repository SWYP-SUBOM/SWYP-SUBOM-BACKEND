package swyp_11.ssubom.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MyPostRequestDto {
    private Integer page = 1;
    private Integer size = 20;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sort = "latest";
}
