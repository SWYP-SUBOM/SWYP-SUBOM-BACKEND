package swyp_11.ssubom.global.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final userDTO userDTO;
    public CustomOAuth2User(userDTO userDTO) {
        if (userDTO == null || userDTO.getKakaoId() == null) {
            throw new IllegalArgumentException("userDTO or kakaoId is null");
        }
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("kakaoId", userDTO.getKakaoId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDTO.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getUserName();
    }

    public String getKakaoId() {
        return userDTO.getKakaoId();
    }


}
