/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.Message;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.logging.Logger;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className EasyMqttCallback
 * @description
 * @date 2024/8/23 20:35
 */
public class EasyMqttCallback implements MqttCallback {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = Logger.getLogger(EasyMqttCallback.class.getName());

    /**
     * mqtt client id
     */
    private final ClientId clientId;

    private final MqttMessageHandler messageHandler;

    public EasyMqttCallback(ClientId clientId, MqttMessageHandler messageHandler) {
        this.clientId = clientId;
        this.messageHandler = messageHandler;
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.warning("MQTT client [" + this.clientId.clientId() + "] connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        messageHandler.handle(new Message(topic, message.getId(), message.getQos(), message.getPayload(), message.isRetained(), message.isDuplicate()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.info("Message delivered to broker: " + token.getMessageId());
    }
}
