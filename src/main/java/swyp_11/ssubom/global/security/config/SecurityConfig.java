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
                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setExposedHeaders(Collections.singletonList("access"));
                configuration.setMaxAge(3600L);
                return configuration;
            }
        }));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(auth->auth.disable());

        http.formLogin(auth->auth.disable());

        http.httpBasic(auth->auth.disable());

        http.
                addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        http.
                addFilterBefore(new CustomLogoutFilter(jwtUtil,refreshRepository), LogoutFilter.class);

        http.
                oauth2Login((oauth2)->oauth2
                .userInfoEndpoint((userInfoEndpointConfig)->userInfoEndpointConfig
                        .userService(customOauth2UserService))
                        .successHandler(customOAuthSuccessHandler)
                        .permitAll());

        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/assets/**", "/favicon.ico", "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/webjars/**", "/swagger/**","/api-docs/**","/images/logo.png","/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth","/","/login","/join","/logout","/api/oauth2-jwt-header","/api/reissue","api/categories","api/home").permitAll()
                        .requestMatchers("/api/my").hasRole("USER")
                        .requestMatchers("/api/writings/**").hasRole("USER")
                        .requestMatchers("/api/feeds/**").hasRole("USER")
                        .requestMatchers("/api/notifications").hasRole("USER")
                        .requestMatchers("/api/categories").hasRole("USER")

                        .anyRequest().authenticated());

        return http.build();
    }

}
