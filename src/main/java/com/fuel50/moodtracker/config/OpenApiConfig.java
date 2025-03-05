package com.fuel50.moodtracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .description("API for tracking team mood and providing analytics on team sentiment")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Fuel50")
                                .url("https://www.fuel50.com")
                                .email("support@fuel50.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.fuel50.com/terms")))
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ));
    }
}