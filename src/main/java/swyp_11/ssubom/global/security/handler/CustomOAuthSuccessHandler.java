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
        Integer expireS = 24*60*60;

        //todo
        //테스트 환경 만료시간 길게  60*10*1000L
        String access = jwtUtil.createJWT("access",kakaoId,role,2L * 24 * 60 * 60 * 1000);
        String refresh =jwtUtil.createJWT("refresh",kakaoId,role,expireS * 1000L);


        refreshTokenService.saveRefresh(kakaoId,refresh,expireS);
//        response.addCookie(CookieUtil.createCookie("access",access,2 * 24 * 60 * 60));
//        response.addCookie(CookieUtil.createCookie("refresh", refresh, expireS));
        ResponseCookie accessCookie = ResponseCookie.from("access", access)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .maxAge(2*24*60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh", refresh)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .maxAge(expireS)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String encodedName = URLEncoder.encode(username, "UTF-8");
        response.sendRedirect("http://localhost:5174/oauth2-jwt-header?name=" + encodedName);
    }
}
