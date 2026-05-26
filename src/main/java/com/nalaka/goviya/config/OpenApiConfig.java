package com.nalaka.goviya.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 *
 * @author - Nalaka
 * @date - 09/04/2026
 */


@Configuration
public class OpenApiConfig {

    @Value("${swagger.server-url}")
    private String swaggerServerUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        Server serverSettings = new Server();
        serverSettings.setUrl(swaggerServerUrl + contextPath);
        //System.out.println(swaggerServerUrl + contextPath);
        return new OpenAPI()
/*                .components(new Components()
                        .addSecuritySchemes("spring_oauth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .name("Bearer Authentication")
                                .bearerFormat("JWT")
                                .scheme("bearer")
                        )
                )
                .security(Collections.singletonList(
                        new SecurityRequirement().addList("spring_oauth")))*/
                .info(new Info()
                        .title("Auth Control Application")
                        .description("This API facilitates the Auth Control Application.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Nalaka Dinesh")
                                .url("https://www.nalaka.online//")
                                .email("nalakadineshx@gmail.com"))).servers(Collections.singletonList(serverSettings));
    }
}
