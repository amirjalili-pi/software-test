package com.amigoscode.testing.payment.domain.repository;

import com.amigoscode.testing.payment.domain.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
