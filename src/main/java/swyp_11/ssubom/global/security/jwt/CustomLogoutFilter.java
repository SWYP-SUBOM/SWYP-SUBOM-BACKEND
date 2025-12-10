package swyp_11.ssubom.global.security.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.GenericFilterBean;
import swyp_11.ssubom.domain.user.repository.RefreshRepository;
import swyp_11.ssubom.global.security.util.CookieUtil;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final  JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private  final CookieUtil cookieUtil;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        if (!requestURI.matches("^/?api/logout/?$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();

        if(requestMethod.equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        if(!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refreshToken")) {
                refresh = cookie.getValue();
            }
        }

        if(refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //expired check
        try{
            jwtUtil.isExpired(refresh);
        }catch(ExpiredJwtException e){
            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //토큰이 refresh 인지 확인 ( 발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refreshToken")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB저장
        Boolean isExist = refreshRepository.existsByRefreshValue(refresh);
        if(!isExist){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        refreshRepository.deleteByRefreshValue(refresh);

        ResponseCookie deleteRefreshCookie = cookieUtil.createCookie("refreshToken", null, 0);
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);



    }
}
