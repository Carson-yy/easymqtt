/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt;

import io.easymqtt.core.MqttClientContainer;
import io.easymqtt.core.MqttClientFactory;
import io.easymqtt.config.MqttConfig;
import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.SubscribeInfo;

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
                true,
                0,
                true
        );

//        String clientId = MqttClientFactory.createAsyncClient(mqttConfig);

        String clientId = MqttClientFactory.createClient(mqttConfig);

        SubscribeInfo subscribeInfo = new SubscribeInfo(
                "$share/dev/device/#",
                0,
                "demo",
                (msg) -> System.out.println(msg)
        );

        MqttClientContainer.subscribe(clientId, subscribeInfo);

        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
