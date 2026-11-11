package com.smartbank.notification.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.smartbank.common.events.CustomerCreatedEvent;
import com.smartbank.common.events.Topics;
import com.smartbank.common.events.TransferCompletedEvent;
import com.smartbank.common.events.TransferFailedEvent;

/**
 * EXEMPLE DE RÉFÉRENCE consommateur Kafka.
 * TODO (notification-service) : persister une Notification en base et envoyer un email (Mailpit),
 * puis ajouter les listeners LOAN_DECIDED et STATEMENT_GENERATED.
 */
@Component
public class BankingEventsListener {

    private static final Logger log = LoggerFactory.getLogger(BankingEventsListener.class);

    @KafkaListener(topics = Topics.CUSTOMER_CREATED)
    public void onCustomerCreated(CustomerCreatedEvent event) {
        log.info("Bienvenue à {} ({})", event.fullName(), event.email());
    }

    @KafkaListener(topics = Topics.TRANSFER_COMPLETED)
    public void onTransferCompleted(TransferCompletedEvent event) {
        log.info("Virement {} confirmé : {} {} de {} vers {}", event.transferId(), event.amount(),
                event.currency(), event.sourceIban(), event.targetIban());
    }

    @KafkaListener(topics = Topics.TRANSFER_FAILED)
    public void onTransferFailed(TransferFailedEvent event) {
        log.info("Virement {} refusé : {}", event.transferId(), event.reason());
    }
}
