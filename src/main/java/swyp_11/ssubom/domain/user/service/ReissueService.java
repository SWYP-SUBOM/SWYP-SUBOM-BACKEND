package swyp_11.ssubom.domain.user.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.domain.user.repository.RefreshRepository;
import swyp_11.ssubom.global.security.util.CookieUtil;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String refresh = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if(refresh == null) {
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }
        try {
            jwtUtil.isExpired(refresh);
        } catch(ExpiredJwtException e){
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refreshToken")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String kakaoId = jwtUtil.getKakaoId(refresh);
        String role = jwtUtil.getRole(refresh);

        Boolean isExist = refreshRepository.existsByRefreshValue(refresh);

        if(!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //todo 엑세스시간 refresh 시간 변경
        String newAccess = jwtUtil.createJWT("accessToken", kakaoId, role, 2 * 24 * 60 * 60);
        int expiredS = 60 * 60 * 24;
        String newRefresh = jwtUtil.createJWT("refreshToken", kakaoId, role, expiredS);

        refreshRepository.deleteByRefreshValue(refresh);
        refreshTokenService.saveRefresh(kakaoId,newRefresh,expiredS);

        response.addHeader("Authorization", "Bearer " + newAccess);
        ResponseCookie newRefreshCookie = CookieUtil.createCookie("refreshToken", newRefresh, expiredS);

        response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
