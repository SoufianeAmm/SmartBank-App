package com.smartbank.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Appel synchrone vers customer-service, résolu via Eureka (name = spring.application.name).
 * Le JWT de l'utilisateur est propagé automatiquement (cf. FeignTokenRelayAutoConfiguration).
 */
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    CustomerSummary getCustomer(@PathVariable("id") Long id);

    record CustomerSummary(Long id, String firstName, String lastName, String email) {
    }
}
