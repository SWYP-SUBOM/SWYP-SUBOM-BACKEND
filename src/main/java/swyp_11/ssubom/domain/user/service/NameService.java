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
        if (user == null) throw new IllegalStateException("인증 필요");
        if(user!=null) {
            user.setUserName(name);
            userRepository.save(user);
        }
    }

    public String getName(String kakaId) {
        User user = userRepository.findByKakaoId(kakaId);
        if (user == null) throw new IllegalStateException("인증 필요");
            return user.getUserName();
    }
}
