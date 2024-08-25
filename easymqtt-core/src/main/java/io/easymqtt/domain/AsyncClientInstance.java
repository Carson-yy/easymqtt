/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className AsyncClientInstance
 * @description
 * @date 2024/8/22 21:24
 */
public record AsyncClientInstance (
        // mqtt client id
        ClientId clientId,

        // mqtt client instance
        MqttAsyncClient mqttClient
) {
}
