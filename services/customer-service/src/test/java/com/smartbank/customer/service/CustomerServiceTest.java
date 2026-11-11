package com.smartbank.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.smartbank.common.events.CustomerCreatedEvent;
import com.smartbank.common.events.Topics;
import com.smartbank.common.web.BusinessException;
import com.smartbank.common.web.ResourceNotFoundException;
import com.smartbank.customer.domain.Customer;
import com.smartbank.customer.domain.CustomerRepository;
import com.smartbank.customer.web.dto.CustomerRequest;
import com.smartbank.customer.web.dto.CustomerResponse;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository repository;

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    CustomerService service;

    private final CustomerRequest request = new CustomerRequest("Sara", "Alaoui", "Sara@Mail.com",
            "AB123456", "+212600000000", "Rabat", LocalDate.of(1995, 3, 10), null);

    @Test
    void create_savesCustomerAndPublishesEvent() {
        when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(repository.existsByNationalId(anyString())).thenReturn(false);
        when(repository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(42L);
            return c;
        });

        CustomerResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("sara@mail.com");
        verify(kafkaTemplate).send(eq(Topics.CUSTOMER_CREATED), eq("42"), any(CustomerCreatedEvent.class));
    }

    @Test
    void create_rejectsDuplicateEmail() {
        when(repository.existsByEmailIgnoreCase("Sara@Mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("email");
        verify(repository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
