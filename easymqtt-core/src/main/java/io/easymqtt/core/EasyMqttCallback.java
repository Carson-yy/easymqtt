/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttCallback
 * @description
 * @date 2024/8/19 20:35
 */
public class EasyMqttCallback implements MqttCallback {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(EasyMqttCallback.class.getName());

    /**
     * mqtt client id
     */
    private final String clientId;

    /**
     *
     * @param clientId mqtt client id
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:44
     */ 
    public EasyMqttCallback(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Method Description: connect lost
     *
     * @param cause throwable
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:45
     */
    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.severe("The mqtt client: " + this.clientId + "is disconnected. cause: " +
                cause.getMessage() +
                """
                ! When set the options. SetAutomaticReconnect (true), will be to reconnect.
                """);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    /**
     * Method Description: delivery complete
     *
     * @param token IMqttDeliveryToken
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:47
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.config("Delivery Complete "+token.isComplete()+", ClientId: "+token.getClient().getClientId()+", topics: " + Arrays.stream(token.getTopics())
                .map(s -> s + ",").collect(StringBuilder::new, StringBuilder::append, StringBuilder::append));
    }
}
