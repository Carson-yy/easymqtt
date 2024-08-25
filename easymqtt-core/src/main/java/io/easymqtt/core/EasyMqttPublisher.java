package io.easymqtt.core;

import io.easymqtt.domain.*;
import io.easymqtt.exceptions.EasyMqttException;

import java.util.concurrent.*;

/**
 * Project Name: easymqtt
 *
 * @author Carson
 * @className EasyMqttPublisher
 * @description mqtt publisher
 * @date 2024/08/25 12:06:52
 */
public final class EasyMqttPublisher {

    private EasyMqttPublisher() {
    }

    /**
     * Method Description: publish
     *
     * @param message publish message
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 12:26
     */
    public static void publish(PublishMessage message) throws EasyMqttException {
        MqttClientContainer.publish(message);
    }

    /**
     * Method Description: publish with response
     *
     * @param message publish message
     * @param callback callback message
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 12:40
     */
    public static void publishWithResponse(PublishMessage message, Callback callback) throws EasyMqttException {
        // first subscribe callback
        final CountDownLatch messageReceived = new CountDownLatch(1);

        MqttClientContainer.subscribe(message.clientId(), callback.topic(), callback.qos(), (msg) -> {
            messageReceived.countDown();
            callback.messageHandler().handle(msg);
        });

        // publish
        publish(message);

        try {
            messageReceived.await(callback.timeout(), TimeUnit.SECONDS);
            MqttClientContainer.unsubscribe(message.clientId(), callback.topic());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
