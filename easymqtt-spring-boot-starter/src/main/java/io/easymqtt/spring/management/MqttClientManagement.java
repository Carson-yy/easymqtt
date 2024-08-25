/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.management;

import io.easymqtt.core.MqttClientContainer;
import io.easymqtt.core.MqttClientFactory;
import io.easymqtt.config.MqttConfig;
import io.easymqtt.domain.SubscribeInfo;
import io.easymqtt.spring.annotations.MqttListener;
import io.easymqtt.spring.annotations.QueueMqttListener;
import io.easymqtt.spring.annotations.ShareMqttListener;
import io.easymqtt.spring.annotations.Topic;
import io.easymqtt.spring.properties.EasyMqttProperties;
import io.easymqtt.spring.properties.MqttClientProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private static final Map<String, List<SubscribeInfo>> SUBSCRIBE_INFOS = new ConcurrentHashMap<>(16);

    /**
     * Method Description: init
     *
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:00
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
        MqttConfig mqttConfig = MQTT_CONFIGS.get(this.environment.resolvePlaceholders(listener.clientId()));
        if(Objects.nonNull(mqttConfig)) {
            String clientId;
            if(listener.async()) {
                clientId = MqttClientFactory.createAsyncClient(mqttConfig);
            } else {
                clientId = MqttClientFactory.createClient(mqttConfig);
            }
            Map<String, Integer> topics = Arrays.stream(listener.topics()).collect(Collectors.toMap((lis) -> this.environment.resolvePlaceholders(lis.topic()), Topic::qos));
            cacheSubscribeInfo(clientId, topics, method, bean);
        }
    }

    /**
     * Method Description: init share topic client
     *
     * @param method handle
     * @param bean obj
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 19:34
     */
    public void initShareClient(Method method, Object bean) {
        ShareMqttListener listener = method.getDeclaredAnnotation(ShareMqttListener.class);
        MqttConfig mqttConfig = MQTT_CONFIGS.get(this.environment.resolvePlaceholders(listener.clientId()));
        if(Objects.nonNull(mqttConfig)) {
            String clientId;
            if(listener.async()) {
                clientId = MqttClientFactory.createAsyncClient(mqttConfig);
            } else {
                clientId = MqttClientFactory.createClient(mqttConfig);
            }
            Map<String, Integer> topics = Arrays.stream(listener.topics()).collect(Collectors.toMap((lis) -> "$share/" + listener.groupId() +"/" + this.environment.resolvePlaceholders(lis.topic()), Topic::qos));
            cacheSubscribeInfo(clientId, topics, method, bean);
        }
    }

    /**
     * Method Description: init queue client
     *
     * @param method handle
     * @param bean obj
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 19:39
     */
    public void initQueueClient(Method method, Object bean) {
        QueueMqttListener listener = method.getDeclaredAnnotation(QueueMqttListener.class);
        MqttConfig mqttConfig = MQTT_CONFIGS.get(this.environment.resolvePlaceholders(listener.clientId()));
        if(Objects.nonNull(mqttConfig)) {
            String clientId;
            if(listener.async()) {
                clientId = MqttClientFactory.createAsyncClient(mqttConfig);
            } else {
                clientId = MqttClientFactory.createClient(mqttConfig);
            }
            Map<String, Integer> topics = Arrays.stream(listener.topics()).collect(Collectors.toMap((lis) -> "$queue/" + this.environment.resolvePlaceholders(lis.topic()), Topic::qos));
            cacheSubscribeInfo(clientId, topics, method, bean);
        }
    }

    /**
     * Method Description: cache subscribe info
     *
     * @param clientId mqtt client id
     * @param topics   subscribe topics
     * @param method   handler
     * @param bean     bean
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 19:25
     */
    private void cacheSubscribeInfo(String clientId, Map<String, Integer> topics, Method method, Object bean) {
        List<SubscribeInfo> subscribeInfoList = topics.entrySet().stream()
                .map(entry -> new SubscribeInfo(entry.getKey(), entry.getValue(), bean.getClass().getCanonicalName() + "." +method.getName(), (msg) -> {
                    try {
                        method.invoke(bean, msg);
                    } catch (Exception e) {
                        LOGGER.severe("Mqtt client method invoke error: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }))
                .toList();
        List<SubscribeInfo> subscribeInfos = SUBSCRIBE_INFOS.get(clientId);
        if(Objects.isNull(subscribeInfos)) {
            SUBSCRIBE_INFOS.put(clientId, new ArrayList<>());
        }
        SUBSCRIBE_INFOS.get(clientId).addAll(subscribeInfoList);
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
