/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.*;
import io.easymqtt.exceptions.EasyMqttException;
import io.easymqtt.handle.MqttMessageHandler;
import io.easymqtt.utils.TopicUtil;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttClientContainer
 * @description
 * @date 2024/8/19 20:48
 */
public final class MqttClientContainer {

    private static final Logger LOGGER = Logger.getLogger(MqttClientContainer.class.getName());

    /**
     * client subscribe info
     */
    private static final Map<String, Map<String, List<SubscribeInfo>>> SUBSCRIBE_TOPICS_MAP = new ConcurrentHashMap<>(16);

    private MqttClientContainer() {
    }

    /**
     * save client & real client id link
     */
    private static final Map<String, String> CLIENT_ID_MAP = new ConcurrentHashMap<>(16);

    /**
     * mqtt client map
     */
    private static final Map<String, Object> CLIENTS = new HashMap<>(16);

    /**
     * Method Description: register client
     *
     * @param clientId mqtt client id
     * @param clientInstance mqtt client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:53
     */
    static void registerClient(ClientId clientId, ClientInstance clientInstance) {
        if(CLIENT_ID_MAP.containsKey(clientId.clientId())) {
            throw new EasyMqttException("Duplicate register client id: " + clientId.clientId());
        }
        CLIENT_ID_MAP.put(clientId.clientId(), clientId.realClientId());
        CLIENTS.put(clientId.clientId(), clientInstance);
    }

    /**
     * Method Description: register async mqtt client
     *
     * @param clientId mqtt client id
     * @param clientInstance async client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 21:25
     */
    static void registerClient(ClientId clientId, AsyncClientInstance clientInstance) {
        if(CLIENT_ID_MAP.containsKey(clientId.clientId())) {
            throw new EasyMqttException("Duplicate register client id: " + clientId.clientId());
        }
        CLIENT_ID_MAP.put(clientId.clientId(), clientId.realClientId());
        CLIENTS.put(clientId.clientId(), clientInstance);
    }

    /**
     * Method Description: subscribe
     *
     * @param clientId 客户端
     * @param subscribeInfo 订阅信息
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/21 22:13
     */
    public static void subscribe(String clientId, SubscribeInfo subscribeInfo) {
        subscribeInfo.validate();
        Object instance = CLIENTS.get(clientId);
        if(Objects.isNull(instance)) {
            LOGGER.warning("Client " + clientId + " instance not found");
            return;
        }

        if(instance instanceof AsyncClientInstance asyncClientInstance) {
            try{
                cacheSubscribeInfo(clientId, subscribeInfo);
                asyncClientInstance.mqttClient().subscribe(subscribeInfo.topic(), subscribeInfo.qos());
                LOGGER.info("Mqtt Async Client [" + clientId + "] subscribe [" + subscribeInfo.topic() + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        }

        if(instance instanceof ClientInstance clientInstance) {
            try{
                cacheSubscribeInfo(clientId, subscribeInfo);
                clientInstance.mqttClient().subscribe(subscribeInfo.topic(), subscribeInfo.qos());
                LOGGER.info("Mqtt Client [" + clientId + "] subscribe [" + subscribeInfo.topic() + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        }
    }

    /**
     * cache client topic and subscribe info
     */
    private static void cacheSubscribeInfo(String clientId, SubscribeInfo subscribeInfo) {
        Map<String, List<SubscribeInfo>> topicSubscribeInfo = SUBSCRIBE_TOPICS_MAP.getOrDefault(clientId, new HashMap<>(16));
        List<SubscribeInfo> subscribeInfoList = topicSubscribeInfo.getOrDefault(subscribeInfo.topic(), new ArrayList<>());
        subscribeInfoList.add(subscribeInfo);
        topicSubscribeInfo.put(subscribeInfo.topic(), subscribeInfoList);
        SUBSCRIBE_TOPICS_MAP.put(clientId, topicSubscribeInfo);
    }

    /**
     * Method Description: disconnected mqtt client
     *
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:31
     */
    public static void disconnected() {
        CLIENTS.forEach((clientId, o) -> {
            if(o instanceof AsyncClientInstance asyncClientInstance) {
                if(asyncClientInstance.mqttClient().isConnected()) {
                    try{
                        asyncClientInstance.mqttClient().disconnect();
                    } catch (Exception ignored) {}
                }
            }
            if(o instanceof ClientInstance clientInstance) {
                if(clientInstance.mqttClient().isConnected()) {
                    try{
                        clientInstance.mqttClient().disconnect();
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    /***
     * Method Description: get subscribe message handler
     *
     * @param clientId mqtt client id
     * @param topic topic
     * @return io.easymqtt.handle.MqttMessageHandler
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 15:36
     */
    public static List<MqttMessageHandler> getMqttMessageHandlers(ClientId clientId, String topic) {
        Map<String, List<SubscribeInfo>> subscribeInfoMap = SUBSCRIBE_TOPICS_MAP.get(clientId.clientId());
        if(Objects.isNull(subscribeInfoMap) || subscribeInfoMap.isEmpty()) {
            return null;
        }
        return subscribeInfoMap.entrySet().stream()
                .filter(entry -> TopicUtil.isMatched(entry.getKey(), topic))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(SubscribeInfo::messageHandler)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Method Description: get client by clientId
     *
     * @param clientId mqtt client id
     * @return io.easymqtt.domain.ClientId
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 18:17
     */
    public static String getClient(String clientId) {
        return CLIENTS.keySet().stream().filter(s -> s.equals(clientId)).findFirst().orElse(null);
    }
}
