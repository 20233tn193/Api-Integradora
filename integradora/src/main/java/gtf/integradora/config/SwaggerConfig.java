package gtf.integradora.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Gestión de Torneos de Fútbol")
                        .version("1.0")
                        .description("Documentación de la API para el sistema Integradora GTF"));
    }

    // @Bean
    // public OpenAPI customOpenAPI() {
    //     return new OpenAPI()
    //             .info(new Info().title("API GTF").version("1.0").description("Documentación de la API"))
    //             .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
    //             .components(new io.swagger.v3.oas.models.Components()
    //                     .addSecuritySchemes("bearerAuth",
    //                             new SecurityScheme()
    //                                     .name("bearerAuth")
    //                                     .type(SecurityScheme.Type.HTTP)
    //                                     .scheme("bearer")
    //                                     .bearerFormat("JWT")));
    // }
}
