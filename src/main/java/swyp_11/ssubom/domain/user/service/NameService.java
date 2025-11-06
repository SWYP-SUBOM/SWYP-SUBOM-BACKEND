package swyp_11.ssubom.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.UserRepository;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

@RequiredArgsConstructor
@Service

public class NameService {
    private final UserRepository userRepository;

    public void saveName(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            user.updateUserName(name);
            userRepository.save(user);
    }

    public String getName(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            return user.getUserName();
    }
}
