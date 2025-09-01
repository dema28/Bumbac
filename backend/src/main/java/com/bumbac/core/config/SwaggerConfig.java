package com.bumbac.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Bumbac.md API")
            .description("Документация к API интернет-магазина пряжи. " +
                "API предоставляет функциональность для управления каталогом, " +
                "пользователями, заказами и другими аспектами интернет-магазина.")
            .version("1.0.0")
            .contact(new Contact()
                .name("Bumbac.md Team")
                .email("support@bumbac.md")
                .url("https://bumbac.md"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .components(new Components()
            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT токен для аутентификации")));
  }
}
