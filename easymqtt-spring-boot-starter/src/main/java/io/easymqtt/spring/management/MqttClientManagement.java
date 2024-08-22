/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.management;

import io.easymqtt.core.MqttClientContainer;
import io.easymqtt.core.MqttClientFactory;
import io.easymqtt.core.MqttConfig;
import io.easymqtt.domain.ClientId;
import io.easymqtt.domain.SubscribeInfo;
import io.easymqtt.spring.annotations.AsyncMqttListener;
import io.easymqtt.spring.annotations.MqttListener;
import io.easymqtt.spring.properties.EasyMqttProperties;
import io.easymqtt.spring.properties.MqttClientProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttClientManagement
 * @description
 * @date 2024/8/22 21:57
 */
@AllArgsConstructor
public class MqttClientManagement {

    private static final Logger LOGGER = Logger.getLogger(MqttClientManagement.class.getName());


    /**
     * mqtt properties
     */
    private final EasyMqttProperties properties;

    /**
     * env
     */
    private final Environment environment;

    /**
     * mqtt configs
     */
    private static final Map<String, MqttConfig> MQTT_CONFIGS = new ConcurrentHashMap<>(16);

    /**
     * subscribe  info
     */
    private static final Map<ClientId, List<SubscribeInfo>> SUBSCRIBE_INFOS = new ConcurrentHashMap<>(16);

    /**
     * Method Description: init
     *
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:00
     */
    private void init() {
        List<MqttClientProperties> clients = this.properties.getClients();
        if(!CollectionUtils.isEmpty(clients)) {
            clients.forEach(client -> {
                MqttConfig mqttConfig = new MqttConfig(
                        client.getId(),
                        client.getAddress(),
                        client.getPort(),
                        client.getUsername(),
                        client.getPassword(),
                        client.getCleanSession(),
                        client.getConnectTimeout(),
                        client.getAutoReconnect()
                );
                MQTT_CONFIGS.put(client.getId(), mqttConfig);
            });
        }
    }

    /**
     * Method Description: destroy mqtt client
     *
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:31
     */
    private void destroy() {
        MqttClientContainer.disconnected();
    }

    /***
     * Method Description:
     *
     * @param method handle
     * @param bean obj
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:21
     */
    public void initClient(Method method, Object bean) {
        MqttListener listener = method.getDeclaredAnnotation(MqttListener.class);
        MqttConfig mqttConfig = MQTT_CONFIGS.get(listener.clientId());
        if(Objects.nonNull(mqttConfig)) {
            ClientId clientId = MqttClientFactory.createClient(mqttConfig);
            List<SubscribeInfo> subscribeInfoList = Arrays.stream(listener.topics())
                    .map(topic -> new SubscribeInfo(this.environment.resolvePlaceholders(topic.topic()), topic.qos(), (msg) -> {
                        try {
                            method.invoke(bean, msg);
                        } catch (Exception e) {
                            LOGGER.severe("Mqtt client method invoke error: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }))
                    .toList();
            SUBSCRIBE_INFOS.put(clientId, subscribeInfoList);
        }
    }

    /***
     * Method Description: init async client
     *
     * @param method handle
     * @param bean obj
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:24
     */
    public void initAsyncClient(Method method, Object bean) {
        AsyncMqttListener listener = method.getDeclaredAnnotation(AsyncMqttListener.class);
        MqttConfig mqttConfig = MQTT_CONFIGS.get(listener.clientId());
        if(Objects.nonNull(mqttConfig)) {
            ClientId clientId = MqttClientFactory.createAsyncClient(mqttConfig);
            List<SubscribeInfo> subscribeInfoList = Arrays.stream(listener.topics())
                    .map(topic -> new SubscribeInfo(topic.topic(), topic.qos(), (msg) -> {
                        try {
                            method.invoke(bean, msg);
                        } catch (Exception e) {
                            LOGGER.severe("Mqtt client method invoke error: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }))
                    .toList();
            SUBSCRIBE_INFOS.put(clientId, subscribeInfoList);
        }
    }

    /**
     * Method Description: do subscribe
     *
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:30
     */
    public void subscribeAction() {
        SUBSCRIBE_INFOS.forEach((clientId, subscribeInfoList) -> subscribeInfoList.forEach(info -> MqttClientContainer.subscribe(clientId, info)));
    }
}
