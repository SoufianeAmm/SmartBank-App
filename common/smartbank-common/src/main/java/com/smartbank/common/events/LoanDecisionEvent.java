package com.smartbank.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanDecisionEvent(
        Long loanRequestId,
        Long customerId,
        BigDecimal amount,
        int durationMonths,
        String decision,
        BigDecimal monthlyPayment,
        Instant occurredAt) {
}
