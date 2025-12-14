package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(getInfo())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );

    }

    private Info getInfo() {
        return new Info()
                .title("Recruitment Management System")
                .version("1.0")
                .description("API documentation for Blog App project using SpringDoc & Swagger UI")
                .contact(new Contact()
                        .name("Kartik")
                        .email("kartikpatel7892@gmail.com")
                        .url("http://localhost:8080/v3/api-docs")
                );
    }
}
