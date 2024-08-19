/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.ClientInstance;
import io.easymqtt.exceptions.EasyMqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttClientContainer
 * @description
 * @date 2024/8/19 20:48
 */
public final class MqttClientContainer {

    private MqttClientContainer() {
    }

    /**
     * mqtt client map
     */
    private static final Map<ClientId, ClientInstance> CLIENTS = new HashMap<>(16);

    /**
     * client id list
     */
    private static final List<String> CLIENT_ID_LIST = new ArrayList<>();

    /**
     * Method Description: register client
     *
     * @param clientId mqtt client id
     * @param clientInstance mqtt client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:53
     */
    public static void registerClient(ClientId clientId, ClientInstance clientInstance) {
        if(CLIENT_ID_LIST.stream().anyMatch(id -> id.equals(clientId.getClientId()))) {
            throw new EasyMqttException("Mqtt client id is already in use");
        }
        CLIENT_ID_LIST.add(clientId.getClientId());
        CLIENTS.put(clientId, clientInstance);
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
        return CLIENTS.entrySet().stream().filter(entry -> entry.getKey().getClientId().equals(clientId))
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
        return CLIENTS.entrySet().stream().filter(entry -> entry.getKey().getRealClientId().equals(realClientId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
