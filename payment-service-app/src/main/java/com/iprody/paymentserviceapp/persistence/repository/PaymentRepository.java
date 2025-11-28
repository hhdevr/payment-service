package com.iprody.paymentserviceapp.persistence.repository;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
