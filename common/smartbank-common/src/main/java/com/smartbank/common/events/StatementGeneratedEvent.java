package com.smartbank.common.events;

import java.time.Instant;

/**
 * @param period mois du relevé au format "yyyy-MM"
 */
public record StatementGeneratedEvent(
        String iban,
        Long customerId,
        String period,
        String fileName,
        Instant occurredAt) {
}
