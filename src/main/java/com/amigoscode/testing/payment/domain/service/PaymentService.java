package com.amigoscode.testing.payment.domain.service;

import com.amigoscode.testing.customer.domain.entity.Customer;
import com.amigoscode.testing.customer.domain.repository.CustomerRepository;
import com.amigoscode.testing.payment.domain.dto.PaymentRequest;
import com.amigoscode.testing.payment.domain.entity.CardPaymentCharge;
import com.amigoscode.testing.payment.domain.entity.Payment;
import com.amigoscode.testing.payment.domain.enumeration.CurrencyEnum;
import com.amigoscode.testing.payment.domain.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;

    private final PaymentRepository paymentRepository;

    private final CardPaymentCharger cardPaymentCharger;

    private final List<CurrencyEnum> supportedCurrencies = Arrays.asList(CurrencyEnum.USD, CurrencyEnum.GBP);

    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard(UUID customerId, PaymentRequest request) {

        Payment payment = request.getPayment();
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (!supportedCurrencies.contains(payment.getCurrency())) {
                throw new IllegalStateException(String.format("currency [%s] does not support", payment.getCurrency()));
            }
            CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(payment.getSource(),
                    payment.getAmount(), payment.getCurrency(), payment.getDescription());
            if (!cardPaymentCharge.isDebited()) {
                throw new IllegalStateException("card does not debited");
            }
            if (payment.getCustomerId() == null) {
                payment.setCustomerId(customer.getId());
            }
            paymentRepository.save(payment);

        }else throw new IllegalStateException(String.format("Customer [%s] does not exist", customerId));


    }
}
