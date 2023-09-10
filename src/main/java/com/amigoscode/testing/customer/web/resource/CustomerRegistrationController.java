package com.amigoscode.testing.customer.web.resource;

import com.amigoscode.testing.customer.domain.dto.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.domain.service.CustomerRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/customer-registration")
public class CustomerRegistrationController {

    private final CustomerRegistrationService customerRegistrationService;

    @Autowired
    public CustomerRegistrationController(CustomerRegistrationService customerRegistrationService) {
        this.customerRegistrationService = customerRegistrationService;
    }

    @PutMapping
    public void registerNewCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        customerRegistrationService.registerNewCustomer(request);

    }
}
