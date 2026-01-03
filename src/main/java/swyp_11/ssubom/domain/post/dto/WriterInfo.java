package swyp_11.ssubom.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WriterInfo {
    private String name;
    private boolean isMe;

    @Builder
    public WriterInfo(String name, boolean isMe) {
        this.name = name;
        this.isMe = isMe;
    }

    public WriterInfo of(String name, boolean isMe) {
        return WriterInfo.builder()
                .name(name)
                .isMe(isMe)
                .build();
    }
}
