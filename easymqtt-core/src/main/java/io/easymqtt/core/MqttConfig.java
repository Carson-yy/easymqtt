/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import java.util.Properties;

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
        boolean cleanSession,
        // mqtt connect timeout
        int connectTimeout,
        // mqtt client automatic reconnect
        boolean automaticReconnect
){

    public static MqttConfig fromProperties(Properties properties) {
        String clientId = properties.getProperty(EasyMqttKey.CLIENT_ID);

        return new MqttConfig(clientId, null, null, null, null, false, 0, true);
    }
}