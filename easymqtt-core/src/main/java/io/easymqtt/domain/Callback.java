/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import io.easymqtt.handle.MqttMessageHandler;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className Callback
 * @description subscribe callback
 * @date 2024/8/25 12:28
 */
public record Callback(
        String topic,
        int qos,
        MqttMessageHandler messageHandler,
        Integer timeout
) {
}
