package yerong.acorn_post_backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "bearerAuth";

        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Acorn Post API")
                        .description("도토리우편 백엔드 API 문서")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(schemeName, bearerAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
