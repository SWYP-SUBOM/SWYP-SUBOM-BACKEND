package swyp_11.ssubom.global.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration

public class WebClientConfig {

    @Value("${registration.admin.key}")
    private String adminKey;

    @Bean
    @Qualifier("kakaoUnlinkWebClient")
    public WebClient kakaoAuthWebClient(){
        String authorization = "KakaoAK " + adminKey;
        return WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .defaultHeader("Authorization", authorization)
                .build();
    }
}
