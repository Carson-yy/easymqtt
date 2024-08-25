/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt;

import io.easymqtt.config.MqttConfig;
import io.easymqtt.core.EasyMqttPublisher;
import io.easymqtt.core.MqttClientFactory;
import io.easymqtt.domain.Callback;
import io.easymqtt.domain.PublishMessage;

import java.nio.charset.StandardCharsets;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className PublishTest
 * @description
 * @date 2024/8/25 12:54
 */
public class PublishTest {

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

        String clientId = MqttClientFactory.createClient(mqttConfig);

//        EasyMqttPublisher.publish(new PublishMessage(
//                "/hello",
//                "Hello".getBytes(StandardCharsets.UTF_8),
//                clientId,
//                0, false
//        ));

        EasyMqttPublisher.publishWithResponse(new PublishMessage(
                "/hello",
                "Hello".getBytes(StandardCharsets.UTF_8),
                clientId,
                0, false
        ), new Callback(
                "result/hello",
                0,
                (msg) -> System.out.println(msg),
                10
        ));
        try {
            Thread.sleep(1000000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
