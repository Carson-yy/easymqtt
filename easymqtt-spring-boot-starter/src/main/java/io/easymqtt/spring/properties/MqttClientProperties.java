/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttClientProperties
 * @description
 * @date 2024/8/22 21:49
 */
@Getter
@Setter
public class MqttClientProperties {

    /**
     * client id
     */
    private String id;

    /**
     * ip or domain
     */
    private String address;

    /**
     * broker port
     */
    private String port;

    /**
     * broker username
     */
    private String username;

    /**
     * broker password
     */
    private String password;

    /**
     * mqtt client clean session
     */
    private Boolean cleanSession;

    /**
     * mqtt connect timeout
     */
    private Integer connectTimeout;

    /**
     * mqtt auto reconnect
     */
    private Boolean autoReconnect;
}
