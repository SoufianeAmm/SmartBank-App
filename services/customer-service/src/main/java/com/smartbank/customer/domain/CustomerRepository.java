package com.smartbank.customer.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNationalId(String nationalId);

    Optional<Customer> findByKeycloakUserId(String keycloakUserId);
}
