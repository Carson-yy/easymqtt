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

public record ClientInstance(
        // mqtt client id
        ClientId clientId,

        // mqtt client instance
        MqttClient mqttClient
) {

}
