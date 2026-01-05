package swyp_11.ssubom.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.domain.admin.dto.AdminLoginRequest;
import swyp_11.ssubom.domain.admin.dto.AdminLoginResponse;
import swyp_11.ssubom.domain.admin.entity.Admin;
import swyp_11.ssubom.domain.admin.repository.AdminRepository;
import swyp_11.ssubom.domain.user.service.RefreshTokenService;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.security.jwt.JWTUtil;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminLoginService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final TotpService totpService;
    private final RefreshTokenService refreshTokenService;

    public AdminLoginResponse login(AdminLoginRequest request) {

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (admin.getLockedUntil() != null && admin.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.ADMIN_LOCKED);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            admin.increaseFail();
            adminRepository.save(admin);
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 2FA 설정 이후는 검증
        if (admin.is2faEnabled()) {
            if (request.getTotpCode() == null || !totpService.verify(admin.getTotpSecret(), request.getTotpCode())) {
                throw new BusinessException(ErrorCode.INVALID_2FA);
            }
        }
        admin.resetFail();
        adminRepository.save(admin);

        String access = jwtUtil.createJWT("accessToken", admin.getEmail(), "ROLE_ADMIN", 80 * 60 * 1000L);

        return new AdminLoginResponse(access);
    }
}