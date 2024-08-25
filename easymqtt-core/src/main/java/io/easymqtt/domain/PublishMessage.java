/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className PublishMessage
 * @description publish message
 * @date 2024/8/25 12:10
 */
public record PublishMessage(
        String topic,
        byte[] payload,
        String clientId,
        int qos,
        boolean isRetained
) {

}
