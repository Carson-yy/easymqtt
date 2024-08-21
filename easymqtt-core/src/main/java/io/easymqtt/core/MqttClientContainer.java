/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.ClientInstance;
import io.easymqtt.exceptions.EasyMqttException;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
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
    private static final Map<ClientId, ClientInstance> CLIENTS = new HashMap<>(16);


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
     * Method Description: subscribe
     *
     * @param clientId 客户端
     * @param topics 订阅topic
     * @param qos qos
     * @param listeners 监听器
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/21 22:13
     */
    public static void subscribe(ClientId clientId, String[] topics, int[] qos, IMqttMessageListener[] listeners) {
        ClientInstance clientInstance = CLIENTS.get(clientId);
        if(Objects.nonNull(clientInstance)) {
            try{
                clientInstance.mqttClient().subscribe(topics, qos, listeners);
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else {
            LOGGER.warning("Client " + clientId.clientId() + " instance not found");
        }
    }

    /**
     * Method Description: get mqtt client instance
     *
     * @param clientId mqtt client id
     * @return io.easymqtt.domain.ClientInstance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 21:14
     */
    public static ClientInstance getClientByClientId(String clientId) {
        return CLIENTS.entrySet().stream().filter(entry -> entry.getKey().clientId().equals(clientId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    /**
     * Method Description: get mqtt client instance
     *
     * @param realClientId real mqtt client id
     * @return io.easymqtt.domain.ClientInstance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 21:14
     */
    public static ClientInstance getClientByRealClientId(String realClientId) {
        return CLIENTS.entrySet().stream().filter(entry -> entry.getKey().clientId().equals(realClientId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }


}
