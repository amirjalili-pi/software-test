package com.amigoscode.testing.payment.domain.service;

import com.amigoscode.testing.payment.domain.entity.CardPaymentCharge;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, CurrencyEnum currency, String description);
}
