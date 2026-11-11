package com.smartbank.customer.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.smartbank.common.security.SmartbankSecurityAutoConfiguration;
import com.smartbank.common.web.SmartbankWebAutoConfiguration;
import com.smartbank.customer.service.CustomerService;
import com.smartbank.customer.web.dto.CustomerResponse;

@WebMvcTest(CustomerController.class)
@ImportAutoConfiguration({SmartbankSecurityAutoConfiguration.class, SmartbankWebAutoConfiguration.class})
class CustomerControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CustomerService service;

    @MockitoBean
    JwtDecoder jwtDecoder;

    private static final String VALID_BODY = """
            {"firstName":"Sara","lastName":"Alaoui","email":"sara@mail.com",
             "nationalId":"AB123456","phone":"+212600000000","dateOfBirth":"1995-03-10"}
            """;

    @Test
    void anonymousRequestIsRejected() throws Exception {
        mvc.perform(get("/api/customers/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void clientCannotCreateCustomer() throws Exception {
        mvc.perform(post("/api/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENT")))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void agentCanCreateCustomer() throws Exception {
        when(service.create(any())).thenReturn(new CustomerResponse(1L, "Sara", "Alaoui", "sara@mail.com",
                "AB123456", "+212600000000", null, LocalDate.of(1995, 3, 10), Instant.now()));

        mvc.perform(post("/api/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void invalidPayloadReturnsProblemDetail() throws Exception {
        mvc.perform(post("/api/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.email").exists());
    }
}
