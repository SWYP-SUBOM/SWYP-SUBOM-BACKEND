package swyp_11.ssubom.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.user.dto.userDTO;

import jakarta.servlet.http.Cookie;


import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access = null;
//        access=request.getHeader("access");
//        if(access==null){
//            filterChain.doFilter(request, response);
//            return;
//        }

        // 1. (Swagger/API용) Authorization 헤더에서 Bearer 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            access = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 값
        }
        // 2. (웹 프론트엔드용) 헤더에 없으면 'access' 쿠키에서 토큰 추출
        else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access")) {
                        access = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // 3. 토큰이 아예 없는 경우 (익명 사용자)
        if (access == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            jwtUtil.isExpired(access);
        }catch(ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(access);

        if(!category.equals("access")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String kakaoId = jwtUtil.getKakaoId(access);
        String role = jwtUtil.getRole(access);

        userDTO userDTO = new userDTO();
        userDTO.setKakaoId(kakaoId);
        userDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);

    }
}
