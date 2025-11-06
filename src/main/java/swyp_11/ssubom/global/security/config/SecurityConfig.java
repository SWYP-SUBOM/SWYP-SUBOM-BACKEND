package swyp_11.ssubom.global.security.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import swyp_11.ssubom.global.security.handler.CustomOAuthSuccessHandler;
import swyp_11.ssubom.global.security.jwt.CustomLogoutFilter;
import swyp_11.ssubom.global.security.jwt.JWTFilter;
import swyp_11.ssubom.global.security.jwt.JWTUtil;
import swyp_11.ssubom.domain.user.repository.RefreshRepository;
import swyp_11.ssubom.domain.user.service.CustomOauth2UserService;


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
                configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
                configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
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

        // 미인증 클라이언트(swagger 등)
        http
                .exceptionHandling(exceptions -> exceptions
                        // '/api/**' 경로로 오는 인증되지 않은 요청은
                        // 로그인 페이지 리디렉션 대신 401 Unauthorized 에러를 반환
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        )
                );


        http.
                oauth2Login((oauth2)->oauth2
                .userInfoEndpoint((userInfoEndpointConfig)->userInfoEndpointConfig
                        .userService(customOauth2UserService))
                        .successHandler(customOAuthSuccessHandler)
                        .permitAll());

        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/assets/**", "/favicon.ico", "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/webjars/**", "/swagger/**","/api-docs/**","/images/logo.png","/v3/api-docs/**", "/actuator/**").permitAll()
                        .requestMatchers("/auth","/","/login","/join","/logout","/api/oauth2-jwt-header","/api/reissue","/api/categories","/api/home").permitAll()
                        .requestMatchers("/api/my").hasRole("USER")
                        .requestMatchers("/api/posts/**").hasRole("USER")
                        .requestMatchers("/api/notifications").hasRole("USER")
                        .requestMatchers("/api/categories").hasRole("USER")

                        .anyRequest().authenticated());

        return http.build();
    }

}
