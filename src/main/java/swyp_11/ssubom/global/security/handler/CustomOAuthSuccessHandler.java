package swyp_11.ssubom.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import swyp_11.ssubom.global.security.util.CookieUtil;

import java.io.IOException;
import java.net.URLEncoder;

@Component
@RequiredArgsConstructor
public class CustomOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

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

        //refresh
        int expireS = 24 * 60 * 60;

        String access = jwtUtil.createJWT("accessToken", kakaoId, role, 2 * 24 * 60 * 60);
        String refresh = jwtUtil.createJWT("refreshToken", kakaoId, role, expireS);

        refreshTokenService.saveRefresh(kakaoId,refresh,expireS);

        ResponseCookie accessCookie = CookieUtil.createCookie("accessToken", access, 2 * 24 * 60 * 60);
        ResponseCookie refreshCookie = CookieUtil.createCookie("refreshToken", refresh, expireS);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String encodedName = URLEncoder.encode(username, "UTF-8");

        // TODO: prod, local에 따라 다르게 url 적용하도록 환경 세팅
//        response.sendRedirect("http://localhost:5174/oauth2-jwt-header?name=" + encodedName);
        response.sendRedirect("https://seobom.site/oauth2-jwt-header?name=" + encodedName);
    }
}
