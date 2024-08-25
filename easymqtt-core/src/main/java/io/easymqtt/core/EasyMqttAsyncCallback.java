/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.handle.MqttMessageHandler;
import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.Message;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className EasyMqttCallback
 * @description
 * @date 2024/8/23 20:35
 */
public class EasyMqttAsyncCallback implements MqttCallback {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = Logger.getLogger(EasyMqttAsyncCallback.class.getName());

    /**
     * mqtt client id
     */
    private final ClientId clientId;

    public EasyMqttAsyncCallback(ClientId clientId) {
        this.clientId = clientId;
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.warning("MQTT client [" + this.clientId.clientId() + "] connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        List<MqttMessageHandler> mqttMessageHandlers = MqttClientContainer.getMqttMessageHandlers(this.clientId, topic);
        if(Objects.nonNull(mqttMessageHandlers)) {
            mqttMessageHandlers.parallelStream().forEach(handler -> handler.handle(
                    new Message(topic, message.getId(), message.getQos(), message.getPayload(), message.isRetained(), message.isDuplicate())
            ));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.info("Message delivered to broker: " + token.getMessageId());
    }
}
