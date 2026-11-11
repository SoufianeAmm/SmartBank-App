package com.smartbank.common.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import feign.RequestInterceptor;

/**
 * Propage automatiquement le JWT de l'utilisateur courant sur les appels OpenFeign
 * entre microservices (token relay).
 *
 * Attention : le SecurityContext est lié au thread. Utiliser Resilience4J via les
 * annotations (@CircuitBreaker, @Retry) sur les méthodes de service, qui s'exécutent
 * dans le même thread, plutôt qu'un TimeLimiter qui change de thread.
 */
@AutoConfiguration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignTokenRelayAutoConfiguration {

    @Bean
    public RequestInterceptor bearerTokenRelayInterceptor() {
        return template -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth
                    && !template.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAuth.getToken().getTokenValue());
            }
        };
    }
}
