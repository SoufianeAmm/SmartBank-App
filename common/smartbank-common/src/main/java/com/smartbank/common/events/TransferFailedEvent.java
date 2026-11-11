package com.smartbank.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferFailedEvent(
        String transferId,
        String sourceIban,
        String targetIban,
        BigDecimal amount,
        String reason,
        Instant occurredAt) {
}
