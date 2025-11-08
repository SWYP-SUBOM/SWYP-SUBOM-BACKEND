package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MyPostResponseDto {
    private List<MyPostItem> items;
    private PageInfoDto pageInfo;
}
