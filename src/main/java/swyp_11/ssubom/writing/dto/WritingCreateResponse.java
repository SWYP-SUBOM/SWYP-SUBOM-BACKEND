package swyp_11.ssubom.writing.dto;

import lombok.Getter;

@Getter
public class WritingCreateResponse {

    private Long writingId;
    private Streak streak;

    public static WritingCreateResponse of(Long writingId, Streak streak) {
        WritingCreateResponse response = new WritingCreateResponse();
        response.writingId = writingId;
        response.streak = streak;
        return response;
    }

    public static WritingCreateResponse ofDraft(Long writingId) {
        WritingCreateResponse response = new WritingCreateResponse();
        response.writingId = writingId;
        response.streak = null;
        return response;
    }


}
