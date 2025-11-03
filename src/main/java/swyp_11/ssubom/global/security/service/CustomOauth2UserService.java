package swyp_11.ssubom.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.global.security.dto.CustomOAuth2User;
import swyp_11.ssubom.global.security.dto.KaKaoResponse;
import swyp_11.ssubom.global.security.dto.OAuth2Response;
import swyp_11.ssubom.global.security.dto.userDTO;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.global.security.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

//        System.out.println("❇️ AccessToken = " + userRequest.getAccessToken().getTokenValue());

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KaKaoResponse(oAuth2User.getAttributes());
        }

        System.out.println(oAuth2Response);

        User existData = userRepository.findByKakaoId(oAuth2Response.getProviderId());

        //새로 생성
        if (existData == null) {
            User user = new User();
            user.setKakaoId(oAuth2Response.getProviderId());
            user.setEmail(oAuth2Response.getEmail());
            user.setUserName(oAuth2Response.getName());
            user.setRole("ROLE_USER");
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);

            userDTO userDTO = new userDTO();
            userDTO.setKakaoId(oAuth2Response.getProviderId());
            userDTO.setRole("ROLE_USER");
            userDTO.setUserName(oAuth2Response.getName());

            return new CustomOAuth2User(userDTO);
        }
        else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUserName(existData.getUserName());

            userRepository.save(existData);

            userDTO userDTO = new userDTO();
            userDTO.setUserName(existData.getUserName());
            userDTO.setRole(existData.getRole());
            userDTO.setKakaoId(existData.getKakaoId());

            return new CustomOAuth2User(userDTO);
        }
    }
}
