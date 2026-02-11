package com.chaykin.paymentserviceapi;

public interface AsyncSender<T extends Message> {

    /**
     * Отправляет сообщение.
     *
     * @param message сообщение для отправки
     */
    void send(T message);
}