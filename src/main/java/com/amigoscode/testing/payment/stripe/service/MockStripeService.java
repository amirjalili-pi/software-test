package com.amigoscode.testing.payment.stripe.service;

import com.amigoscode.testing.payment.domain.entity.CardPaymentCharge;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import com.amigoscode.testing.payment.domain.service.CardPaymentCharger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "false")
public class MockStripeService implements CardPaymentCharger {

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, CurrencyEnum currency, String description) {
        return new CardPaymentCharge(true);
    }
}
