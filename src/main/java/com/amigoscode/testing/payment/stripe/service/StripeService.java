package com.amigoscode.testing.payment.stripe.service;

import com.amigoscode.testing.payment.domain.entity.CardPaymentCharge;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import com.amigoscode.testing.payment.domain.service.CardPaymentCharger;
import com.amigoscode.testing.payment.stripe.api.StripeApi;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "true")
public class StripeService implements CardPaymentCharger {

    private final RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    private final StripeApi stripeApi;

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, CurrencyEnum currency, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put(
                "description",
                description
        );

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            return new CardPaymentCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalStateException("cannot make a stripe charge: " + e);
        }
    }
}
