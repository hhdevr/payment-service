package com.iprody.paymentserviceapp.persistence;

import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static com.iprody.paymentserviceapp.persistence.model.QPayment.payment;
import static com.querydsl.core.types.ExpressionUtils.and;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.springframework.data.domain.Sort.unsorted;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QPaymentFilter {

    private Set<UUID> inquiryRefIds;
    private Set<UUID> transactionRefIds;
    private Set<String> currencies;
    private PaymentStatus status;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private OffsetDateTime createdFrom;
    private OffsetDateTime createdTo;
    private OffsetDateTime updatedFrom;
    private OffsetDateTime updatedTo;
    private Direction directionAmount;
    private Direction directionStatus;
    private Direction directionCreatedAt;
    private Direction directionUpdatedAt;

    @Builder.Default
    private int pageNumber = 0;

    @Builder.Default
    private int pageSize = 25;

    public Predicate createPredicate() {
        Predicate predicate = payment.isNotNull();

        if (!isEmpty(inquiryRefIds)) {
            predicate = and(predicate, payment.inquiryRefId.in(inquiryRefIds));
        }
        if (!isEmpty(transactionRefIds)) {
            predicate = and(predicate, payment.transactionRefId.in(transactionRefIds));
        }
        if (!isEmpty(currencies)) {
            predicate = and(predicate, payment.currency.in(currencies));
        }
        if (status != null) {
            predicate = and(predicate, payment.status.eq(status));
        }
        if (minAmount != null) {
            predicate = and(predicate, payment.amount.goe(minAmount));
        }
        if (maxAmount != null) {
            predicate = and(predicate, payment.amount.loe(maxAmount));
        }
        if (createdFrom != null) {
            predicate = and(predicate, payment.createdAt.goe(createdFrom));
        }
        if (createdTo != null) {
            predicate = and(predicate, payment.createdAt.loe(createdTo));
        }
        if (updatedFrom != null) {
            predicate = and(predicate, payment.updatedAt.goe(updatedFrom));
        }
        if (updatedTo != null) {
            predicate = and(predicate, payment.updatedAt.loe(updatedTo));
        }

        return predicate;
    }

    public Pageable createPageable() {
        return PageRequest.of(pageNumber, pageSize, createSort());
    }

    public Sort createSort() {
        Sort sortOrder = unsorted();

        if (directionAmount != null) {
            sortOrder = sortOrder.and(
                    Sort.by(directionAmount, payment.amount.getMetadata().getName())
            );
        }
        if (directionStatus != null) {
            sortOrder = sortOrder.and(
                    Sort.by(directionStatus, payment.status.getMetadata().getName())
            );
        }
        if (directionCreatedAt != null) {
            sortOrder = sortOrder.and(
                    Sort.by(directionCreatedAt, payment.createdAt.getMetadata().getName())
            );
        }
        if (directionUpdatedAt != null) {
            sortOrder = sortOrder.and(
                    Sort.by(directionUpdatedAt, payment.updatedAt.getMetadata().getName())
            );
        }

        return sortOrder;
    }
}
