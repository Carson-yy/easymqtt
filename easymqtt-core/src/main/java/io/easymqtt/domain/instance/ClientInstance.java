/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain.instance;


import io.easymqtt.domain.ClientId;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className EasyMqttClient
 * @description
 * @date 2024/8/19 20:49
 */
public abstract class ClientInstance<T> {

    /**
     * mqtt client id
     */
    private ClientId clientId;

    /**
     * mqtt client instance
     */
    private T mqttClient;

    public ClientInstance(ClientId clientId, T mqttClient) {
        this.clientId = clientId;
        this.mqttClient = mqttClient;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public T getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(T mqttClient) {
        this.mqttClient = mqttClient;
    }

}
