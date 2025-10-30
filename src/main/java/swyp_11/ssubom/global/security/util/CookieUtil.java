package swyp_11.ssubom.global.security.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    public static Cookie createCookie(String key , String value , Integer expireS){
        Cookie cookie = new Cookie(key,value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expireS);
        return cookie;
    }
}
