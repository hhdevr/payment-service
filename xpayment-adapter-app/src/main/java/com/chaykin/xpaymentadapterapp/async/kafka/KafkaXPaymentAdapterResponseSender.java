package com.chaykin.xpaymentadapterapp.async.kafka;

import com.chaykin.paymentserviceapi.AsyncSender;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterResponseMessage;
import com.chaykin.xpaymentadapterapp.config.kafka.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterResponseSender implements AsyncSender<XPaymentAdapterResponseMessage> {

    private final KafkaProperties kafkaProperties;

    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;

    @Override
    public void send(XPaymentAdapterResponseMessage msg) {
        String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter response: guid={}, amount={}, currency={} -> topic={}",
                 msg.getPaymentGuid(),
                 msg.getAmount(),
                 msg.getCurrency(),
                 kafkaProperties.getResponseTopic());
        template.send(kafkaProperties.getResponseTopic(), key, msg);
    }
}