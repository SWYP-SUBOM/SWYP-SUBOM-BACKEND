package swyp_11.ssubom.domain.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.global.security.util.CookieUtil;

@Service
public class OAuth2JwtHeaderService {
    public String oauth2JwtHeaderSet(HttpServletRequest request, HttpServletResponse response){

        Cookie[] cookies = request.getCookies();
        String access = null;

        if(cookies == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "bad";
        }

        for(Cookie cookie : cookies){
            if(cookie.getName().equals("accessToken")){
                access = cookie.getValue();
            }
        }

        if(access == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "bad";
        }

        ResponseCookie deleteAccessCookie = CookieUtil.createCookie("accessToken",null,0);
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
        response.addHeader("Authorization", "Bearer "+ access);
        response.setStatus(HttpServletResponse.SC_OK);
        return "success";
    }
}
