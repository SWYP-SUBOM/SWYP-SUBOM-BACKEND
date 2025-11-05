package swyp_11.ssubom.domain.user.dto;

public interface OAuth2Response {
    String getProvider(); //kakao
    String getProviderId(); //kakaoId
    String getEmail();
    String getName();
}
