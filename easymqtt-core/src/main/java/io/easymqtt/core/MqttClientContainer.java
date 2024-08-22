/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.*;
import io.easymqtt.exceptions.EasyMqttException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.*;
import java.util.logging.Logger;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttClientContainer
 * @description
 * @date 2024/8/19 20:48
 */
public final class MqttClientContainer {

    private static final Logger LOGGER = Logger.getLogger(MqttClientContainer.class.getName());

    private MqttClientContainer() {
    }

    /**
     * mqtt client map
     */
    private static final Map<ClientId, Object> CLIENTS = new HashMap<>(16);

    /**
     * Method Description: register client
     *
     * @param clientId mqtt client id
     * @param clientInstance mqtt client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:53
     */
    static void registerClient(ClientId clientId, ClientInstance clientInstance) {
        CLIENTS.put(clientId, clientInstance);
    }

    /**
     * Method Description: register async mqtt client
     *
     * @param clientId mqtt client id
     * @param clientInstance async client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 21:25
     */
    static void registerClient(ClientId clientId, AsyncClientInstance clientInstance) {
        CLIENTS.put(clientId, clientInstance);
    }

    /**
     * Method Description: subscribe
     *
     * @param clientId 客户端
     * @param subscribeInfo 订阅信息
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/21 22:13
     */
    public static void subscribe(ClientId clientId, SubscribeInfo subscribeInfo) {
        subscribeInfo.validate();
        Object instance = CLIENTS.get(clientId);
        if(Objects.isNull(instance)) {
            LOGGER.warning("Client " + clientId.clientId() + " instance not found");
            return;
        }

        if(instance instanceof AsyncClientInstance asyncClientInstance) {
            try{
                asyncClientInstance.mqttClient().subscribe(
                        subscribeInfo.topic(),
                        subscribeInfo.qos(),
                        (topic, message) -> subscribeInfo.messageHandler().handle(new Message(topic, message.getId(), message.getQos(), message.getPayload(), message.isRetained(), message.isDuplicate())));
                LOGGER.info("Client " + clientId.clientId() + " subscribe -> " + subscribeInfo.topic());
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        }

        if(instance instanceof ClientInstance clientInstance) {
            try{
                clientInstance.mqttClient().subscribe(
                        subscribeInfo.topic(),
                        subscribeInfo.qos(),
                        (topic, message) -> subscribeInfo.messageHandler().handle(new Message(topic, message.getId(), message.getQos(), message.getPayload(), message.isRetained(), message.isDuplicate())));
                LOGGER.info("Client " + clientId.clientId() + " subscribe -> " + subscribeInfo.topic());
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        }
    }
}
