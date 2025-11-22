package swyp_11.ssubom.domain.post.entity;

public enum AIFeedbackGrade {
    A, B, C, D, F, UNKNOWN;

    public static AIFeedbackGrade fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return AIFeedbackGrade.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN; // 모르는 값이 오면 에러 대신 UNKNOWN 처리
        }
    }
}
