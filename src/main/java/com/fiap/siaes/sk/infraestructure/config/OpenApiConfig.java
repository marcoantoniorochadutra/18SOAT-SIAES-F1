package com.fiap.siaes.sk.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI siaesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIAES API")
                        .description("API do Sistema Integrado de Atendimento e Emissão de Serviços")
                        .version("v1"));
    }
}
