package com.iprody.paymentserviceapp.async.kafka;

import com.chaykin.paymentserviceapi.AsyncListener;
import com.chaykin.paymentserviceapi.MessageHandler;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterMessageListener implements AsyncListener<XPaymentAdapterResponseMessage> {

    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    @Override
    public void onMessage(XPaymentAdapterResponseMessage message) {
        handler.handle(message);
    }

    @KafkaListener(
            topics = "${app.kafka.topics.xpayment-adapter.response-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(XPaymentAdapterResponseMessage message,
                        ConsumerRecord<String, XPaymentAdapterResponseMessage> record,
                        Acknowledgment acknowledgment) {
        try {
            log.info("Received XPayment Adapter response: paymentGuid = {}, status = {}, partition = {}, offset = {}",
                     message.getPaymentGuid(), message.getStatus(),
                     record.partition(), record.offset());
            onMessage(message);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter response for paymentGuid = {}", message.getPaymentGuid(), e);
            throw e;
        }
    }
}
