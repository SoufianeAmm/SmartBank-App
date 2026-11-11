package com.smartbank.customer.web;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartbank.customer.service.CustomerService;
import com.smartbank.customer.web.dto.CustomerRequest;
import com.smartbank.customer.web.dto.CustomerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Clients")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @Operation(summary = "Créer un client fictif (agent/admin)")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/customers/" + created.id())).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @Operation(summary = "Lister les clients (paginé)")
    public Page<CustomerResponse> findAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/me")
    @Operation(summary = "Profil du client connecté")
    public CustomerResponse me(@AuthenticationPrincipal Jwt jwt) {
        return service.findByKeycloakUserId(jwt.getSubject());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @Operation(summary = "Détail d'un client")
    public CustomerResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @Operation(summary = "Modifier un client")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return service.update(id, request);
    }
}
