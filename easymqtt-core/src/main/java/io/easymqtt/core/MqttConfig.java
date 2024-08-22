/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;


import java.util.Optional;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttConfig
 * @description
 * @date 2024/8/19 21:24
 */

public record MqttConfig(
        // mqtt client id
        String clientId,
        // mqtt broker ip/domain
        String address,
        // mqtt broker port
        String port,
        // mqtt broker username
        String username,
        // mqtt broker password
        String password,
        // mqtt client clean session
        Boolean cleanSession,
        // mqtt connect timeout
        Integer connectTimeout,
        // mqtt client automatic reconnect
        Boolean automaticReconnect
){

    public void validate() {
        Optional.ofNullable(clientId).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(address).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(port).orElseThrow(IllegalArgumentException::new);
    }
}