package swyp_11.ssubom.global.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.global.security.repository.RefreshRepository;
import swyp_11.ssubom.global.security.util.CookieUtil;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class reissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        refresh= Arrays.stream(cookies).filter(c -> c.getName().equals("refresh"))
                .findFirst().get().getValue();

        if(refresh == null) {
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }
        try {
            jwtUtil.isExpired(refresh);
        } catch(ExpiredJwtException e){
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String kakaoId = jwtUtil.getKakaoId(refresh);
        String role = jwtUtil.getRole(refresh);

        Boolean isExist = refreshRepository.existsByRefreshValue(refresh);

        if(!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String newAccess = jwtUtil.createJWT("access", kakaoId, role, 60 * 10 * 1000L);
        Integer expiredS = 60 * 60 * 24;
        String newRefresh = jwtUtil.createJWT("refresh", kakaoId, role, expiredS * 1000L);

        refreshRepository.deleteByRefreshValue(refresh);
        refreshTokenService.saveRefresh(kakaoId,newRefresh,expiredS);

        response.setHeader("access", newAccess);
        response.addCookie(CookieUtil.createCookie("refresh", newRefresh, expiredS));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
