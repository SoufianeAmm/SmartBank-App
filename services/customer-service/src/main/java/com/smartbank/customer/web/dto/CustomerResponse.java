package com.smartbank.customer.web.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.smartbank.customer.domain.Customer;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String nationalId,
        String phone,
        String address,
        LocalDate dateOfBirth,
        Instant createdAt) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(),
                c.getNationalId(), c.getPhone(), c.getAddress(), c.getDateOfBirth(), c.getCreatedAt());
    }
}
