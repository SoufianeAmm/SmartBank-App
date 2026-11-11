package com.smartbank.common.events;

import java.time.Instant;

public record AccountCreatedEvent(
        Long accountId,
        String iban,
        Long customerId,
        String accountType,
        Instant occurredAt) {
}
