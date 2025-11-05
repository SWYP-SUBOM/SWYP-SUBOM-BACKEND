package swyp_11.ssubom.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.domain.user.entity.User;
import swyp_11.ssubom.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class NameService {
    private final UserRepository userRepository;

    public void saveName(String kakaId, String name) {
        User user = userRepository.findByKakaoId(kakaId);
        if (user == null) throw new IllegalStateException("사용자의 인증정보 필요");
        if(user!=null) {
            user.updateUserName(name);
            userRepository.save(user);
        }
    }

    public String getName(String kakaId) {
        User user = userRepository.findByKakaoId(kakaId);
        if (user == null) throw new IllegalStateException("인증 필요");
            return user.getUserName();
    }
}
