package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyReactedPostResponseDto {
    private List<MyReactedPostItem> items;
    private SliceInfoDto sliceInfo;
}
