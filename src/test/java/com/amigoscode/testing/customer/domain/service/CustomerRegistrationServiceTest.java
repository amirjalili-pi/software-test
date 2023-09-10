package com.amigoscode.testing.customer.domain.service;

import com.amigoscode.testing.customer.domain.dto.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.domain.entity.Customer;
import com.amigoscode.testing.customer.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @Mock
    private CustomerRepository customerRepository;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "000099";
        Customer customer = new Customer(id, "maryam", phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(request);
        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptureValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptureValue).isEqualToComparingFieldByField(customer);
    }


    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "maryam", phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(request);
        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptureValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptureValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptureValue.getId()).isNotNull();

    }


    @Test
    void itShouldNotSaveNewCustomerWhenCustomerExist() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "1111";
        Customer customer = new Customer(id, "amirmahdi", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        //When

        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should(never()).save(any());
//        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
//                .hasMessageContaining(String.format("this phoneNumber: [%s] is taken", phoneNumber))
//                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void itShouldThrowWhenPhoneNumberTaken() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "1111";
        Customer customer = new Customer(id, "amirmahdi", phoneNumber);
        Customer anotherCustomer = new Customer(UUID.randomUUID(), "mamad", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(anotherCustomer));
        //When


        //Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .hasMessageContaining(String.format("this phoneNumber: [%s] is taken", phoneNumber))
                .isInstanceOf(IllegalStateException.class);

        //Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}