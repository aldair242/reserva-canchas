package com.reservacanchas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static {
        io.swagger.v3.oas.models.media.Schema<String> localTimeSchema = new io.swagger.v3.oas.models.media.Schema<>();
        localTimeSchema.setType("string");
        localTimeSchema.setFormat("time");
        localTimeSchema.setExample("18:00:00");
        localTimeSchema.setPattern("^([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
        org.springdoc.core.utils.SpringDocUtils.getConfig().replaceWithSchema(java.time.LocalTime.class, localTimeSchema);
    }

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI reservaCanchasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reserva de Canchas Deportivas - API")
                        .description("API REST de reserva de canchas deportivas con estadisticas del jugador y highlights de partidos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Reserva Canchas")
                                .email("soporte@reservacanchas.com")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .schemaRequirement(BEARER_AUTH, new SecurityScheme()
                        .name(BEARER_AUTH)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token JWT de autenticacion"));
    }
}
