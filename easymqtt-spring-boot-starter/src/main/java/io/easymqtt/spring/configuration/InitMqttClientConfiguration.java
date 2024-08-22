/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.configuration;

import io.easymqtt.spring.management.MqttClientManagement;
import io.easymqtt.spring.properties.EasyMqttProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className InitMqttClientConfiguration
 * @description
 * @date 2024/8/22 21:47
 */
@Configuration
public class InitMqttClientConfiguration {

    /**
     * Method Description: init mqtt client management
     *
     * @param mqttProperties mqtt properties
     * @return io.easymqtt.spring.management.MqttClientManagement
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 21:58
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    public MqttClientManagement mqttClientManagement(EasyMqttProperties mqttProperties, Environment environment) {
        return new MqttClientManagement(mqttProperties, environment);
    }

    /***
     * Method Description: spring boot service run success
     *
     * @param mqttClientManagement mqtt management
     * @return org.springframework.boot.CommandLineRunner
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:28
     */
    @Bean
    public CommandLineRunner commandLineRunner(MqttClientManagement mqttClientManagement) {
        return args -> mqttClientManagement.subscribeAction();
    }
}
