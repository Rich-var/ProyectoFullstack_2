package cl.duoc.login.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion central de OpenAPI/Swagger para el microservicio Auth.
 *
 * Sin este bean, Swagger UI muestra los endpoints pero no permite probar
 * ninguno protegido por JWT: no existe boton "Authorize" porque no hay
 * un SecurityScheme registrado. Con este bean:
 *  - Aparece el boton "Authorize" en la UI.
 *  - Cada endpoint protegido muestra el candado.
 *  - Al autorizar con el token (sin el prefijo "Bearer "), Swagger lo
 *    agrega automaticamente al header Authorization de cada request.
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Auth Service API")
                .description("Microservicio Auth - Proyecto Cafeteria (DSY1103)")
                .version("v1"))
            .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
            .components(new Components()
                .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                    .name(BEARER_SCHEME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
