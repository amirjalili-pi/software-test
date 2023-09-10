package com.amigoscode.testing.customer.domain.repository;

import com.amigoscode.testing.customer.domain.dto.CustomerRegistrationRequest;
import com.amigoscode.testing.customer.domain.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.properties.javax.persistence.validation.mode=none")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "1111";
        Customer customer = new Customer(id, "Amirmahdi", phoneNumber);
        //When
        underTest.save(customer);
        //Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Amirmahdi", "1111");
        Customer fakeCustomer = new Customer(UUID.randomUUID(), "amir", "0000");
        //When
        underTest.save(customer);
        //Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(customer.getId());
//                    assertThat(c.getName()).isEqualTo(customer.getName());
//                    assertThat(c.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneWhenPhoneNumberDoesNotExist() {
        //Given
        String phoneNumber = "0000";
        //When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        //Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "1111");
        //When
        //Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.domain.entity.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "akbar", null);
        //When
        //Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.domain.entity.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}