/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain.instance;

import io.easymqtt.domain.ClientId;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className AsyncClientInstance
 * @description
 * @date 2024/8/22 21:24
 */
public class AsyncClientInstance extends ClientInstance<MqttAsyncClient> {

    public AsyncClientInstance(ClientId clientId, MqttAsyncClient mqttClient) {
        super(clientId, mqttClient);
    }
}
