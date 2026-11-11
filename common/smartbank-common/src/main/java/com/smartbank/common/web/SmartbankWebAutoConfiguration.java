package com.smartbank.common.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SmartbankWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /** Swagger UI avec bouton "Authorize" pour coller un JWT Keycloak. */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "io.swagger.v3.oas.models.OpenAPI")
    static class OpenApiConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public OpenAPI smartbankOpenApi(@Value("${spring.application.name:smartbank}") String appName) {
            return new OpenAPI()
                    .info(new Info().title("SmartBank - " + appName).version("v1")
                            .description("Simulation pédagogique - aucune transaction réelle"))
                    .components(new Components().addSecuritySchemes("bearerAuth",
                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }
    }
}
