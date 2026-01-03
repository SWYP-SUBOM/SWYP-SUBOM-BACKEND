package swyp_11.ssubom.global.security.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class temp implements CommandLineRunner {
        private final PasswordEncoder passwordEncoder;
        @Override
        public void run(String... args) {
            System.out.println(passwordEncoder.encode("Admin123!"));
        }
    }

