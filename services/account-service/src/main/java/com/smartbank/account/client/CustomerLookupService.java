package com.smartbank.account.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smartbank.common.web.BusinessException;
import com.smartbank.common.web.ResourceNotFoundException;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

/**
 * EXEMPLE DE RÉFÉRENCE Resilience4J (à reproduire dans transfer-service et loan-service) :
 * - @Retry : réessaie 3 fois en cas d'erreur réseau/5xx,
 * - @CircuitBreaker : coupe les appels si customer-service échoue trop souvent,
 * - fallback : réponse dégradée et message clair au lieu d'une erreur 500.
 * Les paramètres (seuils, délais) sont dans le Config Server (resilience4j.*.instances.customer-service).
 */
@Service
public class CustomerLookupService {

    private static final Logger log = LoggerFactory.getLogger(CustomerLookupService.class);

    private final CustomerClient customerClient;

    public CustomerLookupService(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @CircuitBreaker(name = "customer-service", fallbackMethod = "customerServiceDown")
    @Retry(name = "customer-service")
    public CustomerClient.CustomerSummary requireCustomer(Long customerId) {
        try {
            return customerClient.getCustomer(customerId);
        } catch (FeignException.NotFound e) {
            // 404 = erreur métier, pas une panne : ne doit pas déclencher retry/circuit breaker
            throw new ResourceNotFoundException("Client", customerId);
        }
    }

    @SuppressWarnings("unused")
    private CustomerClient.CustomerSummary customerServiceDown(Long customerId, Throwable cause) {
        if (cause instanceof ResourceNotFoundException notFound) {
            throw notFound;
        }
        log.warn("customer-service indisponible pour le client {} : {}", customerId, cause.toString());
        throw new BusinessException("CUSTOMER_SERVICE_UNAVAILABLE",
                "Impossible de vérifier le client pour le moment, réessayez plus tard");
    }
}
