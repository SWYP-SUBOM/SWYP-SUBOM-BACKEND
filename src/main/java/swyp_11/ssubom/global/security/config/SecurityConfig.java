package swyp_11.ssubom.global.security.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import swyp_11.ssubom.global.security.handler.CustomOAuthSuccessHandler;
import swyp_11.ssubom.global.security.jwt.CustomLogoutFilter;
import swyp_11.ssubom.global.security.jwt.JWTFilter;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.global.security.repository.RefreshRepository;
import swyp_11.ssubom.global.security.service.CustomOauth2UserService;


import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOAuthSuccessHandler customOAuthSuccessHandler;
    private final RefreshRepository refreshRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true); // 쿠키 포함 허용
                configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                configuration.setExposedHeaders(List.of("access", "Authorization", "set-Cookie")); // 응답에서 노출할 헤더
                configuration.setMaxAge(3600L);
                return configuration;
            }
        }));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(auth->auth.disable());

        http.formLogin(auth->auth.disable());

        http.httpBasic(auth->auth.disable());

        http.addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        http.addFilterBefore(new CustomLogoutFilter(jwtUtil,refreshRepository), LogoutFilter.class);

        http.
                oauth2Login((oauth2)->oauth2
                .userInfoEndpoint((userInfoEndpointConfig)->userInfoEndpointConfig
                        .userService(customOauth2UserService))
                        .successHandler(customOAuthSuccessHandler)
                        .permitAll());

        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/assets/**", "/favicon.ico", "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/webjars/**", "/swagger", "/index.html","/api-docs/**","/images/logo.png").permitAll()
                        .requestMatchers("/auth","/","/login","/join","/logout","/oauth2-jwt-header","/reissue").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

}
