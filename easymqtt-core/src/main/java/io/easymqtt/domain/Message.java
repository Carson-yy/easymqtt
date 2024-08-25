/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className Message
 * @description
 * @date 2024/8/22 21:00
 */
public record Message(
        String topic,
        int id,
        int qos,
        byte[] message,
        boolean retained,
        boolean duplicate
) {

    /**
     * Method Description: convert MqttMessage to Message
     *
     * @param topic topic
     * @param mqttMessage org.eclipse.paho.client.mqttv3.MqttMessage
     * @return io.easymqtt.domain.Message
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:36
     */
    public static Message ofMqttMessage(String topic, MqttMessage mqttMessage) {
        return new Message(
                topic,
                mqttMessage.getId(),
                mqttMessage.getQos(),
                mqttMessage.getPayload(),
                mqttMessage.isRetained(),
                mqttMessage.isDuplicate()
        );
    }

}
