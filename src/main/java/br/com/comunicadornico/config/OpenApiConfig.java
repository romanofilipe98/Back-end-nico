package br.com.comunicadornico.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nicoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Nico - Comunicador Aumentativo Alternativo")
                        .description("API REST do Nico: gerenciamento de administradores, usuários, botões e categorias.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipe Nico")))
                // define o esquema "BearerAuth" usado nos @SecurityRequirement dos controllers
                // (sem addSecurityItem global: só as rotas com @SecurityRequirement mostram o cadeado)
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}