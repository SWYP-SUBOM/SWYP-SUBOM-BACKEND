package swyp_11.ssubom.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.global.security.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.global.security.repository.UserRepository;
import swyp_11.ssubom.global.security.service.RefreshTokenService;
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
        String access = jwtUtil.createJWT("access",kakaoId,role,60*10*1000L);
        String refresh =jwtUtil.createJWT("refresh",kakaoId,role,expireS * 1000L);

        refreshTokenService.saveRefresh(kakaoId,refresh,expireS);
        response.addCookie(CookieUtil.createCookie("access",access,60*10));
        response.addCookie(CookieUtil.createCookie("refresh", refresh, expireS));

        String encodedName = URLEncoder.encode(username, "UTF-8");
        response.sendRedirect("http://localhost:3000/oauth2-jwt-header?name=" + encodedName);
    }
}
