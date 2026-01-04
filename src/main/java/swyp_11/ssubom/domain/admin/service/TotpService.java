package swyp_11.ssubom.domain.admin.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

    public String generateSecret() {
        return authenticator.createCredentials().getKey();
    }

    public boolean verify(String secret, String code) {
        return authenticator.authorize(secret, Integer.parseInt(code));
    }
}
