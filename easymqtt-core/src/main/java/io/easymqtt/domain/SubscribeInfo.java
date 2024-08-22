/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import io.easymqtt.core.MqttMessageHandler;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className SubscribeInfo
 * @description
 * @date 2024/8/22 20:57
 */
public record SubscribeInfo(
        String topic,
        int qos,
        MqttMessageHandler messageHandler
) {
    public void validate() {
        Optional.ofNullable(topic).orElseThrow(() -> new IllegalArgumentException("topic is null"));
        Optional.ofNullable(messageHandler).orElseThrow(() -> new IllegalArgumentException("messageHandler is null"));
    }
}
