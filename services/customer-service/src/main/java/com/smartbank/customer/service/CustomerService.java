package com.smartbank.customer.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartbank.common.events.CustomerCreatedEvent;
import com.smartbank.common.events.Topics;
import com.smartbank.common.web.BusinessException;
import com.smartbank.common.web.ResourceNotFoundException;
import com.smartbank.customer.domain.Customer;
import com.smartbank.customer.domain.CustomerRepository;
import com.smartbank.customer.web.dto.CustomerRequest;
import com.smartbank.customer.web.dto.CustomerResponse;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CustomerService(CustomerRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("EMAIL_ALREADY_USED", "Un client existe déjà avec cet email", HttpStatus.CONFLICT);
        }
        if (repository.existsByNationalId(request.nationalId())) {
            throw new BusinessException("NATIONAL_ID_ALREADY_USED", "Un client existe déjà avec ce CIN", HttpStatus.CONFLICT);
        }
        Customer customer = new Customer();
        apply(customer, request);
        customer.setKeycloakUserId(request.keycloakUserId());
        Customer saved = repository.save(customer);

        // NB : en production on utiliserait le pattern "transactional outbox".
        kafkaTemplate.send(Topics.CUSTOMER_CREATED, String.valueOf(saved.getId()),
                new CustomerCreatedEvent(saved.getId(), saved.getKeycloakUserId(),
                        saved.getFirstName() + " " + saved.getLastName(), saved.getEmail(), Instant.now()));
        return CustomerResponse.from(saved);
    }

    public Page<CustomerResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(CustomerResponse::from);
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(get(id));
    }

    public CustomerResponse findByKeycloakUserId(String keycloakUserId) {
        return repository.findByKeycloakUserId(keycloakUserId)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Profil client", keycloakUserId));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = get(id);
        if (!customer.getEmail().equalsIgnoreCase(request.email())
                && repository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("EMAIL_ALREADY_USED", "Un client existe déjà avec cet email", HttpStatus.CONFLICT);
        }
        apply(customer, request);
        return CustomerResponse.from(customer);
    }

    private Customer get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    private static void apply(Customer c, CustomerRequest r) {
        c.setFirstName(r.firstName());
        c.setLastName(r.lastName());
        c.setEmail(r.email().toLowerCase());
        c.setNationalId(r.nationalId());
        c.setPhone(r.phone());
        c.setAddress(r.address());
        c.setDateOfBirth(r.dateOfBirth());
    }
}
