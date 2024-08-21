/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt;

import io.easymqtt.core.MqttClientContainer;
import io.easymqtt.core.MqttClientFactory;
import io.easymqtt.core.MqttConfig;
import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.ClientInstance;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className StartTest
 * @description
 * @date 2024/8/21 20:36
 */
public class StartTest {


    public static void main(String[] args) {
        MqttConfig mqttConfig = new MqttConfig(
              "demo",
              "tcp://127.0.0.1",
              "1883",
              null,
                null,
                false,
                0,
                true
        );

         ClientId clientId = MqttClientFactory.createClient(mqttConfig);

         IMqttMessageListener messageListener = (topic, message) -> {
             System.out.println("topic: " + topic + " message: " + message);
         };

         MqttClientContainer.subscribe(clientId, new String[]{ "$SYS/#" }, new int[]{ 1 }, new IMqttMessageListener[] {messageListener});

        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
