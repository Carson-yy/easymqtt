/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className EasyMqttProperties
 * @description easy mqtt properties
 * @date 2024/8/22 21:47
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "easymqtt")
public class EasyMqttProperties {

    /**
     * spring boot config mqtt clients
     */
    List<MqttClientProperties> clients;

}
