package com.amigoscode.testing.payment.domain.integration;

import com.amigoscode.testing.customer.domain.dto.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.domain.entity.Customer;
import com.amigoscode.testing.payment.domain.dto.PaymentRequest;
import com.amigoscode.testing.payment.domain.entity.Payment;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import com.amigoscode.testing.payment.domain.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;
    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "amirmahdi", "9999");
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        ResultActions customerRegistrationResultAction = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(mapObjectToString(customerRegistrationRequest)))
        );
        Payment payment = new Payment(1L, customerId, new BigDecimal("10.00"), CurrencyEnum.USD, "debit", "payment");
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        //When
        ResultActions paymentChargeResultAction = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(mapObjectToString(paymentRequest))));

        //Then
        customerRegistrationResultAction.andExpect(status().isOk());

        paymentChargeResultAction.andExpect(status().isOk());

        assertThat(paymentRepository.findById(payment.getPaymentId())).isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));
    }

    private String mapObjectToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("cannot convert object to string");
        }
        return null;
    }
}
