package com.amigoscode.testing.payment.domain.repository;

import com.amigoscode.testing.payment.domain.entity.Payment;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.properties.javax.persistence.validation.mode=none")
class PaymentRepositoryTest {



    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldSavePayment() {
        //Given
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, UUID.randomUUID(), new BigDecimal("10.00"),
                CurrencyEnum.USD, "debitCard", "donation");
        //When
        underTest.save(payment);
        //Then
        Optional<Payment> optionalPayment = underTest.findById(paymentId);
        assertThat(optionalPayment).isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));
    }
}