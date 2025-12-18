package com.iprody.paymentserviceapp.persistence;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public final class PaymentSpecifications {

    public static Specification<Payment> hasCurrency(String currency) {
        return (root,
                query,
                cb)
                -> cb.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> amountBetween(BigDecimal min,
                                                       BigDecimal max) {
        return (root,
                query,
                cb) -> cb.between(root.get("amount"), min, max);
    }

    public static Specification<Payment> createdBetween(Instant after,
                                                        Instant before) {
        return (root,
                query,
                cb) -> cb.between(root.get("createdAt"), after, before);
    }
}
