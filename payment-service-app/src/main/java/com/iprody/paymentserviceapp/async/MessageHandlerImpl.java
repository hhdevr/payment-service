package com.iprody.paymentserviceapp.async;

import com.iprody.paymentserviceapp.exception.ServiceException;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.iprody.paymentserviceapp.exception.ErrorMessage.STATUS_NOT_MATCH;

@Slf4j
@Component
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterResponseMessage> {

    private static final Map<XPaymentAdapterStatus, PaymentStatus> STATUS_MAP =
            Map.of(XPaymentAdapterStatus.PROCESSING, PaymentStatus.PENDING,
                   XPaymentAdapterStatus.CANCELED, PaymentStatus.DECLINED,
                   XPaymentAdapterStatus.SUCCEEDED, PaymentStatus.APPROVED);

    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        final PaymentStatus status = STATUS_MAP.get(message.getStatus());

        if (status == null) {
            log.warn("Unknown status {}, payment {} ignored",
                     message.getStatus(), message.getPaymentGuid());
            throw new ServiceException(STATUS_NOT_MATCH, message.getPaymentGuid());
        }

        log.info("Update payment {} status: {} -> {}",
                 message.getPaymentGuid(),
                 message.getStatus(),
                 status);
        paymentService.updateStatus(message.getPaymentGuid(), status);
    }
}
