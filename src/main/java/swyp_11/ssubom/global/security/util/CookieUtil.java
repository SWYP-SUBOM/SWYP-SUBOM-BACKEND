package swyp_11.ssubom.global.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${cookie.domain}")
    private String domain;

    @Value("${cookie.secure}")
    private boolean secure;

    @Value("${cookie.same-site}")
    private String sameSite;

    public ResponseCookie createCookie(String key , String value , Integer expireS){
        return ResponseCookie.from(key, value)
                .domain(domain)
                .path("/")
                .sameSite(sameSite)
                .secure(secure)
                .httpOnly(true)
                .maxAge(expireS)
                .build();
    }
}
