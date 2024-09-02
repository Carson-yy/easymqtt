/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.Message;
import io.easymqtt.domain.instance.ClientInstance;
import io.easymqtt.domain.instance.GenericClientInstance;
import io.easymqtt.handle.MqttMessageHandler;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
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

    public EasyMqttCallback(ClientId clientId) {
        this.clientId = clientId;
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.warning("MQTT client [" + this.clientId.clientId() + "] connection lost! because " + cause.getMessage());
        // get mqtt client
        Object client = MqttClientContainer.getClient(this.clientId.clientId());

        // convert mqtt client to ClientInstance
        // TODO add Monitor to mqtt client
        if(Objects.nonNull(client) && client instanceof ClientInstance clientInstance) {
            if(clientInstance instanceof GenericClientInstance genericClientInstance) {
                MqttClient mqttClient = genericClientInstance.getMqttClient();
                if(Objects.nonNull(mqttClient)) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            if(mqttClient.isConnected()) {
                                timer.cancel();
                            }
                        }
                        
                    }, 0, 1000);
                }
            }
        }
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
        LOGGER.info("Message delivered to broker messageId: " + token.getMessageId()
                    + ", send topic: " + String.join(",", token.getTopics())
                    + ", is Completed");
    }
}
