package com.amigoscode.testing.payment.domain.dto;

import com.amigoscode.testing.payment.domain.entity.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    private final Payment payment;

    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "payment=" + payment +
                '}';
    }
}
