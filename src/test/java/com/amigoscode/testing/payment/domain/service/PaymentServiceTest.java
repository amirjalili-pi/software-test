package com.amigoscode.testing.payment.domain.service;

import com.amigoscode.testing.customer.domain.entity.Customer;
import com.amigoscode.testing.customer.domain.repository.CustomerRepository;
import com.amigoscode.testing.payment.domain.dto.PaymentRequest;
import com.amigoscode.testing.payment.domain.entity.CardPaymentCharge;
import com.amigoscode.testing.payment.domain.entity.Payment;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import com.amigoscode.testing.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class PaymentServiceTest {



    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Captor
    private ArgumentCaptor<CardPaymentCharge> cardPaymentChargeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;


    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Amirmahdi", "9999");
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
        Payment payment = new Payment(null, null, new BigDecimal("10.00"), CurrencyEnum.USD, "debitCard", "forDonate");
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        given(cardPaymentCharger.chargeCard(payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription())).willReturn(new CardPaymentCharge(true));

        //When
        underTest.chargeCard(customerId, paymentRequest);
        //Then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue).isEqualToIgnoringGivenFields(payment, "customerId");
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCardIsNotDebited() {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Amirmahdi", "9999");
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
        Payment payment = new Payment(null, null, new BigDecimal("10.00"), CurrencyEnum.USD, "debitCard", "forDonate");
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        
        given(cardPaymentCharger.chargeCard(payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription())).willReturn(new CardPaymentCharge(false));

        //When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .hasMessageContaining("card does not debited")
                .isInstanceOf(IllegalStateException.class);
        //Then
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Amirmahdi", "9999");
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
        CurrencyEnum currency = CurrencyEnum.EUR;
        Payment payment = new Payment(null, null, new BigDecimal("10.00"), currency, "debitCard", "forDonate");
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        //When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .hasMessageContaining(String.format("currency [%s] does not support", payment.getCurrency()))
                .isInstanceOf(IllegalStateException.class);
        //Then
        then(cardPaymentCharger).shouldHaveNoInteractions();

        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCustomerIsNull() {
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());


        //When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .hasMessageContaining(String.format("Customer [%s] does not exist", customerId))
                .isInstanceOf(IllegalStateException.class);
        //Then
        then(cardPaymentCharger).shouldHaveNoInteractions();

        then(paymentRepository).shouldHaveNoInteractions();
    }
}