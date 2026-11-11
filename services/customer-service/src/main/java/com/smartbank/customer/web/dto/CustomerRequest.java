package com.smartbank.customer.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^[A-Z]{1,2}[0-9]{4,8}$", message = "format CIN invalide (ex: AB123456)")
        String nationalId,
        @Pattern(regexp = "^\\+?[0-9 ]{8,15}$", message = "numéro de téléphone invalide") String phone,
        @Size(max = 255) String address,
        @Past LocalDate dateOfBirth,
        /** Optionnel : id Keycloak de l'utilisateur à lier à ce client. */
        String keycloakUserId) {
}
