/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain.instance;

import io.easymqtt.domain.ClientId;
import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className SyncClientInstance
 * @description
 * @date 2024/8/25 16:14
 */
public class GenericClientInstance extends ClientInstance<MqttClient> {

    public GenericClientInstance(ClientId clientId, MqttClient mqttClient) {
        super(clientId, mqttClient);
    }
}
