package com.smartbank.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferCompletedEvent(
        String transferId,
        String sourceIban,
        String targetIban,
        BigDecimal amount,
        String currency,
        String label,
        Instant occurredAt) {
}
