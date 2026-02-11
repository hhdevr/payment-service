package com.chaykin.paymentserviceapi;

/**
 * Статусы в которых может пребывать платежная транзакция X Payment
 * Adapter.
 */
public enum XPaymentAdapterStatus {
    PROCESSING,
    CANCELED,
    SUCCEEDED
}
