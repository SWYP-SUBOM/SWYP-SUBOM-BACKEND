package swyp_11.ssubom.domain.user.dto;

import java.util.Map;

public class KaKaoResponse implements OAuth2Response {

    private final Map<String , Object> attributes;
    private final Map<String , Object> kakaoAccount;

    public KaKaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String , Object>) attributes.get("kakao_account");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        if (kakaoAccount == null) return null;
        return  kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        return "no";
    }
}
