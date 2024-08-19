/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.Serializable;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className EasyMqttClient
 * @description
 * @date 2024/8/19 20:49
 */
public class ClientInstance implements Serializable {

    /**
     * mqtt client id
     */
    private ClientId clientId;

    /**
     * mqtt client instance
     */
    private MqttClient mqttClient;

    /**
     * Method Description: 
     *
     * @param clientId mqtt client id
     * @param mqttClient mqtt client
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:58
     */ 
    public ClientInstance(ClientId clientId, MqttClient mqttClient) {
        this.clientId = clientId;
        this.mqttClient = mqttClient;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Override
    public String toString() {
        return "ClientInstance{" +
                "clientId=" + clientId +
                ", mqttClient=" + mqttClient +
                '}';
    }
}
