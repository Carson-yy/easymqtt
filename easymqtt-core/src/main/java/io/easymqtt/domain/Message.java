/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className Message
 * @description
 * @date 2024/8/22 21:00
 */
public record Message(
        String topic,
        int id,
        int qos,
        byte[] message,
        boolean retained,
        boolean duplicate
) {
}
