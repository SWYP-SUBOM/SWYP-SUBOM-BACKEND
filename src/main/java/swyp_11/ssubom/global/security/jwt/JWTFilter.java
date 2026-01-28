package swyp_11.ssubom.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import swyp_11.ssubom.domain.user.dto.CustomOAuth2User;
import swyp_11.ssubom.domain.admin.dto.AdminDetails;
import swyp_11.ssubom.domain.user.dto.userDTO;
import swyp_11.ssubom.domain.admin.entity.Admin;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.admin.repository.AdminRepository;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository,AdminRepository adminRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository=userRepository;
        this.adminRepository=adminRepository;
    }

    // Swagger 및 인증 불필요한 경로는 JWT 필터 건너뛰기
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/actuator") ||
                path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access = null;

        // 1. (Swagger/API용) Authorization 헤더에서 Bearer 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            access = authorizationHeader.substring(7);
        }

        // 2. (웹 프론트엔드용) 헤더에 없으면 'access' 쿠키에서 토큰 추출
        else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
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
            request.setAttribute("auth_error", "ACCESS_TOKEN_EXPIRED");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        String category = jwtUtil.getCategory(access);

        if(!category.equals("accessToken")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String principal = jwtUtil.getprincipal(access);
        String role = jwtUtil.getRole(access);
        Authentication authToken;

        if("ROLE_ADMIN".equals(role)){
            Admin admin = adminRepository.findByEmail(principal)
                    .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

            AdminDetails adminDetails = new AdminDetails(admin);
            authToken = new UsernamePasswordAuthenticationToken(
                    adminDetails,
                    null,
                    adminDetails.getAuthorities()
            );

        }else{
            User userEntity = userRepository.findByKakaoId(principal);
            if(userEntity==null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            userDTO userDTO = new userDTO();
            userDTO.setKakaoId(principal);
            userDTO.setUserId(userEntity.getUserId());
            userDTO.setRole(role);
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
            authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        }

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);

    }
}
