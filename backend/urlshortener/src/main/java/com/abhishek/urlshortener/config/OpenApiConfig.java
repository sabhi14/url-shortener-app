package com.abhishek.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for the URL Shortener API.
 * Exposes API documentation and Swagger UI at /swagger-ui.html when the application is running.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI urlShortenerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("URL Shortener API")
                        .description("REST API for shortening long URLs and resolving short codes. "
                                + "Create short links and retrieve original URLs by short code.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("URL Shortener Team")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Demo")
                                .url("https://example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server")));
    }
}
