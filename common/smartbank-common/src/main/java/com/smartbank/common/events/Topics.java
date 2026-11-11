package com.smartbank.common.events;

/**
 * Noms des topics Kafka. Toute nouvelle constante doit être ajoutée ici
 * pour que producteurs et consommateurs partagent le même contrat.
 */
public final class Topics {

    public static final String CUSTOMER_CREATED = "smartbank.customer.created";
    public static final String ACCOUNT_CREATED = "smartbank.account.created";
    public static final String TRANSFER_COMPLETED = "smartbank.transfer.completed";
    public static final String TRANSFER_FAILED = "smartbank.transfer.failed";
    public static final String LOAN_DECIDED = "smartbank.loan.decided";
    public static final String STATEMENT_GENERATED = "smartbank.statement.generated";

    private Topics() {
    }
}
