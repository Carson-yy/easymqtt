/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.domain.*;
import io.easymqtt.domain.instance.AsyncClientInstance;
import io.easymqtt.domain.instance.ClientInstance;
import io.easymqtt.domain.instance.GenericClientInstance;
import io.easymqtt.exceptions.EasyMqttException;
import io.easymqtt.handle.MqttMessageHandler;
import io.easymqtt.utils.TopicUtil;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
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
    private static final Map<String, ClientInstance<?>> CLIENTS = new HashMap<>(16);

    /**
     * Method Description: register client
     *
     * @param clientId mqtt client id
     * @param clientInstance mqtt client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:53
     */
    static <T> void registerClient(ClientId clientId, ClientInstance<T> clientInstance) {
        if(CLIENT_ID_MAP.containsKey(clientId.clientId())) {
            throw new EasyMqttException("Duplicate register client id: " + clientId.clientId());
        }
        CLIENT_ID_MAP.put(clientId.clientId(), clientId.realClientId());
        CLIENTS.put(clientId.clientId(), clientInstance);
    }

    /**
     * Method Description: subscribe
     *
     * @param clientId mqtt client id
     * @param subscribeInfo subscribe info
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/21 22:13
     */
    public static void subscribe(String clientId, SubscribeInfo subscribeInfo) {
        subscribeInfo.validate();
        ClientInstance<?> instance = CLIENTS.get(clientId);
        if(Objects.isNull(instance)) {
            LOGGER.warning("Client " + clientId + " instance not found");
            return;
        }

        if(instance instanceof AsyncClientInstance asyncClientInstance) {
            try{
                cacheSubscribeInfo(clientId, subscribeInfo);
                asyncClientInstance.getMqttClient().subscribe(subscribeInfo.topic(), subscribeInfo.qos());
                LOGGER.info("Mqtt Async Client [" + clientId + "] subscribe [" + subscribeInfo.topic() + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else if(instance instanceof GenericClientInstance genericClientInstance) {
            try{
                cacheSubscribeInfo(clientId, subscribeInfo);
                genericClientInstance.getMqttClient().subscribe(subscribeInfo.topic(), subscribeInfo.qos());
                LOGGER.info("Mqtt Client [" + clientId + "] subscribe [" + subscribeInfo.topic() + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else {
            throw new EasyMqttException("Unsupported mqtt client instance type: " + instance.getClass());
        }
    }

    /**
     * Method Description: subscribe internal
     *
     * @param clientId mqtt client id
     * @param topic subscribe topic
     * @param qos qos
     * @param consumer message consumer
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:51
     */
    static void subscribe(String clientId, String topic, int qos, Consumer<Message> consumer) {
        ClientInstance<?> instance = CLIENTS.get(clientId);
        if(Objects.isNull(instance)) {
            LOGGER.warning("Client " + clientId + " instance not found");
            return;
        }

        if(instance instanceof AsyncClientInstance asyncClientInstance) {
            try{
                asyncClientInstance.getMqttClient().subscribe(topic, qos, (tp, msg) -> consumer.accept(Message.ofMqttMessage(tp, msg)));
                LOGGER.info("Mqtt Async Client [" + clientId + "] subscribe [" + topic + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else if(instance instanceof GenericClientInstance genericClientInstance) {
            try{
                genericClientInstance.getMqttClient().subscribe(topic, qos,  (tp, msg) -> consumer.accept(Message.ofMqttMessage(tp, msg)));
                LOGGER.info("Mqtt Client [" + clientId + "] subscribe [" + topic + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else {
            throw new EasyMqttException("Unsupported mqtt client instance type: " + instance.getClass());
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
     * Method Description: clean cache subscribe info
     *
     * @param clientId mqtt client id
     * @param topic topic
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:31
     */
    private static void cleanSubscribeInfo(String clientId, String topic) {
        Map<String, List<SubscribeInfo>> topicSubscribeInfo = SUBSCRIBE_TOPICS_MAP.get(clientId);
        if(Objects.nonNull(topicSubscribeInfo)) {
            topicSubscribeInfo.remove(topic);
        }
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
                if(asyncClientInstance.getMqttClient().isConnected()) {
                    try{
                        asyncClientInstance.getMqttClient().disconnect();
                    } catch (Exception ignored) {}
                }
            }
            if(o instanceof GenericClientInstance genericClientInstance) {
                if(genericClientInstance.getMqttClient().isConnected()) {
                    try{
                        genericClientInstance.getMqttClient().disconnect();
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
    static List<MqttMessageHandler> getMqttMessageHandlers(ClientId clientId, String topic) {
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
    static String getClientId(String clientId) {
        return CLIENTS.keySet().stream().filter(s -> s.equals(clientId)).findFirst().orElse(null);
    }

    /**
     * Method Description: get mqtt instance
     *
     * @param clientId mqtt client id
     * @return java.lang.Object
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 12:14
     */
    public static Object getClient(String clientId) {
        return CLIENTS.get(clientId);
    }

    /**
     * Method Description:
     *
     * @param clientId mqtt client id
     * @param topic unsubscribe topic
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:26
     */
    public static void unsubscribe(String clientId, String topic) {
        Object object = getClient(clientId);
        try {
            LOGGER.info("Mqtt client " + clientId + " unsubscribe [" + topic + "]");
            if(object instanceof GenericClientInstance genericClientInstance) {
                genericClientInstance.getMqttClient().unsubscribe(topic);
            }
            if(object instanceof AsyncClientInstance asyncClientInstance) {
                asyncClientInstance.getMqttClient().unsubscribe(topic);
            }
            // clean cache subscribe
            cleanSubscribeInfo(clientId, topic);
        } catch (MqttException e) {
            throw new EasyMqttException(e.getMessage());
        }
    }

    /**
     * Method Description: publish mqtt
     *
     * @param message publish message
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:54
     */
    static void publish(PublishMessage message) {
        Object object = getClient(message.clientId());
        if(object instanceof GenericClientInstance genericClientInstance) {
            try {
                genericClientInstance.getMqttClient().publish(message.topic(), message.payload(), message.qos(), message.isRetained());
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        }

        if(object instanceof AsyncClientInstance asyncClientInstance) {
            try {
                asyncClientInstance.getMqttClient().publish(message.topic(), message.payload(), message.qos(), message.isRetained());
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        }
    }
}
