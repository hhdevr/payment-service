package com.iprody.paymentserviceapp.async;

public interface MessageHandler<T extends Message> {

    /**
     * Обрабатывает переданное сообщение.
     *
     * @param message сообщение для обработки
     */
    void handle(T message);
}
