package swyp_11.ssubom.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.domain.user.service.RefreshTokenService;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.global.security.util.CookieUtil;

import java.io.IOException;
import java.net.URLEncoder;

@Component
@RequiredArgsConstructor
public class CustomOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${oauth.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String kakaoId = customOAuth2User.getKakaoId();

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username ; //실제 이름

        User userEntity = userRepository.findByKakaoId(kakaoId);


        if(!userEntity.getUserName().equals("no")) {
            username = userEntity.getUserName();
        }
        else{
            username =customOAuth2User.getName();
        }


        Integer expireS = 2 * 24 * 60 * 60; // 2*24시간

        String access = jwtUtil.createJWT("accessToken", kakaoId, role,  60 * 60 *1000L);
        String refresh = jwtUtil.createJWT("refreshToken", kakaoId, role, expireS*1000L);

        refreshTokenService.saveRefresh(kakaoId,refresh,expireS);

        ResponseCookie accessCookie = cookieUtil.createCookie("accessToken", access, 2 * 60 * 60);
        ResponseCookie refreshCookie = cookieUtil.createCookie("refreshToken", refresh, expireS);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String encodedName = URLEncoder.encode(username, "UTF-8");

        response.sendRedirect(redirectUrl + "?name=" + encodedName);
    }
}
