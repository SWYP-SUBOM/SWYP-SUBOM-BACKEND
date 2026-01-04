package swyp_11.ssubom.global.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.domain.admin.dto.AdminDetails;
import swyp_11.ssubom.domain.admin.entity.Admin;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

@Component
public class SecurityUtil {
    public Admin getCurrentAdmin() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AdminDetails)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_FOUND);
        }

        return ((AdminDetails) authentication.getPrincipal()).getAdmin();
    }
}
