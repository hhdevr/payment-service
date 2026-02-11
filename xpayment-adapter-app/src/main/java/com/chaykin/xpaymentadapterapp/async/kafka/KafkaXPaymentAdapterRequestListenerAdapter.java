package com.chaykin.xpaymentadapterapp.async.kafka;

import com.chaykin.paymentserviceapi.AsyncListener;
import com.chaykin.paymentserviceapi.MessageHandler;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaXPaymentAdapterRequestListenerAdapter implements AsyncListener<XPaymentAdapterRequestMessage> {

    private final MessageHandler<XPaymentAdapterRequestMessage> handler;

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.x-payment-adapter.request}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(XPaymentAdapterRequestMessage message,
                        ConsumerRecord<String, XPaymentAdapterRequestMessage> record,
                        Acknowledgment ack) {
        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition = {}, offset = {}",
                     message.getPaymentGuid(),
                     record.partition(),
                     record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid = {}",
                      message.getPaymentGuid(),
                      e);
            throw e;
        }
    }
}