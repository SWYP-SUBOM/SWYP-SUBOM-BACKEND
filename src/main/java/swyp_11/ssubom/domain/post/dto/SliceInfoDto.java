package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SliceInfoDto {
    private boolean hasNext; // 다음 페이지가 있는지
    private Long nextCursorId; // 다음 요청 시 사용할 커서 ID
}