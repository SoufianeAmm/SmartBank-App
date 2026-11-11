package com.smartbank.common.events;

import java.time.Instant;

public record CustomerCreatedEvent(
        Long customerId,
        String keycloakUserId,
        String fullName,
        String email,
        Instant occurredAt) {
}
