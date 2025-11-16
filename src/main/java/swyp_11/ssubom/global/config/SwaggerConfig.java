package swyp_11.ssubom.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    final String securitySchemeName = "bearerAuth";
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("🖊️ 써봄  API 명세서")
                        .description("자기계발형 글쓰기 AI 코칭 서비스")
                        .version("v1.0.0")
                )
                // TODO: Local, Prod 에 따라 나눠지게 만들기
                .servers(List.of(
                        new Server()
                                .url("https://api.seobom.site")
                                .description("배포 서버"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발용 서버")
                ))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
