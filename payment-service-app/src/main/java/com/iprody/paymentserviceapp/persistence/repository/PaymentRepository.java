package com.iprody.paymentserviceapp.persistence.repository;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>,
                                           JpaSpecificationExecutor<Payment>,
                                           QuerydslPredicateExecutor<Payment> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Payment e SET e.note = :note WHERE e.guid = :id")
    int updateNote(@Param("id") UUID id, @Param("note") String note);

}
