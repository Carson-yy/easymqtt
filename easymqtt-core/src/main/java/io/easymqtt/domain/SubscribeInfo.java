/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import io.easymqtt.handle.MqttMessageHandler;

import java.util.Optional;

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
        String handlerName,
        MqttMessageHandler messageHandler
) {
    public void validate() {
        Optional.ofNullable(topic).orElseThrow(() -> new IllegalArgumentException("topic is null"));
        Optional.ofNullable(messageHandler).orElseThrow(() -> new IllegalArgumentException("messageHandler is null"));
    }
}
