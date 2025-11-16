package swyp_11.ssubom.global.security.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie createCookie(String key , String value , Integer expireS){
        return ResponseCookie.from(key, value)
                .domain(".seobom.site")
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .maxAge(expireS)
                .build();
    }
}
