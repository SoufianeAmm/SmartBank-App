package com.smartbank.gateway.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/** Réponse renvoyée quand le circuit breaker d'une route est ouvert ou en timeout. */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/{service}")
    public Mono<ProblemDetail> fallback(@PathVariable String service) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Le service " + service + " est momentanément indisponible. Réessayez plus tard.");
        pd.setProperty("code", "SERVICE_UNAVAILABLE");
        pd.setProperty("service", service);
        return Mono.just(pd);
    }
}
