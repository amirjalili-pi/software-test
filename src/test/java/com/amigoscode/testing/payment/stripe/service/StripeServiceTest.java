package com.amigoscode.testing.payment.stripe.service;

import com.amigoscode.testing.payment.domain.entity.CardPaymentCharge;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import com.amigoscode.testing.payment.stripe.api.StripeApi;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @Captor
    private ArgumentCaptor<Map<String, Object>> requestMapArgumentCaptor;

    @Captor
    private ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);

    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //Given
        String cardSource = "x3z24z45";
        BigDecimal amount = new BigDecimal("12.21");
        CurrencyEnum currency = CurrencyEnum.EUR;
        String description = "fetrie";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);
        //When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);

        //Then
        then(stripeApi).should().create(requestMapArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());
        Map<String, Object> requestMapArgumentCaptorValue = requestMapArgumentCaptor.getValue();
        RequestOptions requestOptionsArgumentCaptorValue = requestOptionsArgumentCaptor.getValue();

        assertThat(requestMapArgumentCaptorValue.keySet().size()).isEqualTo(4);
        assertThat(requestMapArgumentCaptorValue.get("amount")).isEqualTo(amount);
        assertThat(requestMapArgumentCaptorValue.get("currency")).isEqualTo(currency);
        assertThat(requestMapArgumentCaptorValue.get("source")).isEqualTo(cardSource);
        assertThat(requestMapArgumentCaptorValue.get("description")).isEqualTo(description);

        assertThat(requestOptionsArgumentCaptorValue).isNotNull();

        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isDebited()).isTrue();
    }
}