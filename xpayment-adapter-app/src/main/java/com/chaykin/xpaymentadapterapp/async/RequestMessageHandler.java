package com.chaykin.xpaymentadapterapp.async;

import com.chaykin.paymentserviceapi.AsyncSender;
import com.chaykin.paymentserviceapi.MessageHandler;
import com.chaykin.paymentserviceapi.XPaymentAdapterStatus;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterRequestMessage;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterResponseMessage;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void handle(XPaymentAdapterRequestMessage requestMessage) {

        scheduler.schedule(() -> {
            XPaymentAdapterResponseMessage message = new XPaymentAdapterResponseMessage();
            message.setMessageId(UUID.randomUUID());
            message.setPaymentGuid(requestMessage.getPaymentGuid());
            message.setAmount(requestMessage.getAmount());
            message.setCurrency(requestMessage.getCurrency());
            message.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            message.setTransactionRefId(UUID.randomUUID());
            message.setOccurredAt(OffsetDateTime.now());

            log.warn("Sending XPayment Adapter message: {}",
                     message.getMessageId());
            sender.send(message);
        }, 10, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
