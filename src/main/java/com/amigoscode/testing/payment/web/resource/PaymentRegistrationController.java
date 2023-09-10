package com.amigoscode.testing.payment.web.resource;


import com.amigoscode.testing.payment.domain.dto.PaymentRequest;
import com.amigoscode.testing.payment.domain.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentRegistrationController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentRegistrationController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public void charge(@RequestBody PaymentRequest paymentRequest) {
        paymentService.chargeCard(paymentRequest.getPayment().getCustomerId(), paymentRequest);
    }
}
