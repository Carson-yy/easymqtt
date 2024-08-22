/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.configuration;

import io.easymqtt.spring.annotations.AsyncMqttListener;
import io.easymqtt.spring.annotations.MqttListener;
import io.easymqtt.spring.management.MqttClientManagement;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className LoadListenerBeanProcessor
 * @description
 * @date 2024/8/22 21:43
 */
@AllArgsConstructor
@Configuration
public class LoadListenerBeanProcessor implements BeanPostProcessor {


    /**
     * mqtt management
     */
    private final MqttClientManagement mqttClientManagement;

    /***
     * Method Description:
     *
     * @param bean bean
     * @param beanName bean name
     * @return java.lang.Object
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:22
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(MqttListener.class)){
                this.mqttClientManagement.initClient(method, bean);
            }
            if(method.isAnnotationPresent(AsyncMqttListener.class)) {
                this.mqttClientManagement.initAsyncClient(method, bean);
            }
        }
        return bean;
    }
}
