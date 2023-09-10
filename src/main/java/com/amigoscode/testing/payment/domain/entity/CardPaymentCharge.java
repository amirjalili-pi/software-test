package com.amigoscode.testing.payment.domain.entity;

public class CardPaymentCharge {

    private final boolean isDebited;

    public CardPaymentCharge(boolean isDebited) {
        this.isDebited = isDebited;
    }

    public boolean isDebited() {
        return isDebited;
    }
}
