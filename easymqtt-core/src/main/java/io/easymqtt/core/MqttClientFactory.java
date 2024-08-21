/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.ClientInstance;
import io.easymqtt.exceptions.EasyMqttException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttClientFactory
 * @description
 * @date 2024/8/19 20:54
 */
public final class MqttClientFactory {

    private MqttClientFactory() {}

    /**
     * Method Description: create mqtt client
     *
     * @param mqttConfig mqtt client config
     * @author Carson yangbaopan@gmail.com
     * @return io.easymqtt.domain.ClientId
     * @date 2024/8/19 21:09
     */
    public static ClientId createClient(MqttConfig mqttConfig) throws EasyMqttException {
        try {
            String realClientId = mqttConfig.clientId() + "_" + Math.abs(UUID.randomUUID().hashCode());
            MqttClient client = new MqttClient(mqttConfig.address() + ":" + mqttConfig.port(), realClientId, new MemoryPersistence());

            // MQTT connect options
            MqttConnectOptions connOpts = getMqttConnectOptions(mqttConfig);

            // connect
            client.connect(connOpts);

            ClientId clientId =new ClientId(mqttConfig.clientId(), realClientId);

            ClientInstance clientInstance = new ClientInstance(clientId, client);

            MqttClientContainer.registerClient(clientId, clientInstance);
            return clientId;
        } catch (Exception e) {
            throw new EasyMqttException(e.getMessage());
        }
    }

    /***
     * Method Description:
     *
     * @param mqttConfig mqtt config
     * @return org.eclipse.paho.client.mqttv3.MqttConnectOptions
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 21:29
     */
    private static MqttConnectOptions getMqttConnectOptions(MqttConfig mqttConfig) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        if(StringUtils.isNoneBlank(mqttConfig.username())) {
            connOpts.setUserName(mqttConfig.username());
        }
        if(StringUtils.isNoneBlank(mqttConfig.password())) {
            connOpts.setPassword(mqttConfig.password().toCharArray());
        }

        // set clean Session
        connOpts.setCleanSession(mqttConfig.cleanSession());
        // set connect timeout
        connOpts.setConnectionTimeout(mqttConfig.connectTimeout());
        // open automatic reconnect
        connOpts.setAutomaticReconnect(mqttConfig.automaticReconnect());
        return connOpts;
    }
}
