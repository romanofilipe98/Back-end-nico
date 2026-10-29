package br.com.comunicadornico.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
                        .contact(new Contact().name("Equipe Nico")));
    }
}