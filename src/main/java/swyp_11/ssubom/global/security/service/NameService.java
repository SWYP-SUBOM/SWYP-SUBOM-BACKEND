package swyp_11.ssubom.global.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp_11.ssubom.global.security.entity.UserEntity;
import swyp_11.ssubom.global.security.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class NameService {
    private final UserRepository userRepository;

    public void saveName(String kakaId, String name) {
        UserEntity user = userRepository.findByKakaoId(kakaId);
        if (user == null) throw new IllegalStateException("인증 필요");
        if(user!=null) {
            user.setUserName(name);
            userRepository.save(user);
        }
    }

    public String getName(String kakaId) {
        UserEntity user = userRepository.findByKakaoId(kakaId);
        if (user == null) throw new IllegalStateException("인증 필요");
            return user.getUserName();
    }
}
