package swyp_11.ssubom.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    private final String securitySchemeName = "bearerAuth";

    @Value("${swagger.server.url}")
    private String serverUrl;

    @Value("${swagger.server.description}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("🖊️ 써봄  API 명세서")
                        .description("자기계발형 글쓰기 AI 코칭 서비스")
                        .version("v1.0.0")
                )
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(serverDescription)
                ))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
