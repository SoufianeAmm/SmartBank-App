package com.smartbank.customer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.smartbank.common.events.Topics;

/** Chaque service crée les topics qu'il PRODUIT. */
@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic customerCreatedTopic() {
        return TopicBuilder.name(Topics.CUSTOMER_CREATED).partitions(3).replicas(1).build();
    }
}
