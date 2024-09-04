/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core;

import io.easymqtt.core.cache.EasyMqttCacheFactory;
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

    private MqttClientContainer() {
    }

    /**
     * save client & real client id link
     */
    private static final ConcurrentHashMap<String, String> CLIENT_ID_MAP = new ConcurrentHashMap<>(16);


    /**
     * Method Description: register client
     *
     * @param clientId       mqtt client id
     * @param clientInstance mqtt client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:53
     */
    static <T> void registerClient(ClientId clientId, ClientInstance<T> clientInstance) {
        if (CLIENT_ID_MAP.containsKey(clientId.clientId())) {
            throw new EasyMqttException("Duplicate register client id: " + clientId.clientId());
        }
        CLIENT_ID_MAP.put(clientId.clientId(), clientId.realClientId());

        // cache mqtt client
        EasyMqttCacheFactory.addClient(clientInstance);
    }

    /**
     * Method Description: subscribe
     *
     * @param clientId      mqtt client id
     * @param subscribeInfo subscribe info
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/21 22:13
     */
    public static void subscribe(String clientId, SubscribeInfo subscribeInfo) {
        // validate subscribe info
        subscribeInfo.validate();

        // get mqtt client from cache
        ClientInstance<?> instance = EasyMqttCacheFactory.getClient(clientId);

        // exception
        EasyMqttException exception = null;

        if (instance instanceof AsyncClientInstance asyncClientInstance) {
            try {
                asyncClientInstance.getMqttClient().subscribe(subscribeInfo.topic(), subscribeInfo.qos());
                LOGGER.info("Mqtt Async Client [" + clientId + "] subscribe [" + subscribeInfo.topic() + "]");
            } catch (MqttException e) {
                exception = new EasyMqttException(e.getMessage());
            }
        } else if (instance instanceof GenericClientInstance genericClientInstance) {
            try {
                genericClientInstance.getMqttClient().subscribe(subscribeInfo.topic(), subscribeInfo.qos());
                LOGGER.info("Mqtt Client [" + clientId + "] subscribe [" + subscribeInfo.topic() + "]");
            } catch (MqttException e) {
                exception = new EasyMqttException(e.getMessage());
            }
        } else {
            exception = new EasyMqttException("Unsupported mqtt client instance type: " + instance.getClass());
        }
        if (exception != null) {
            throw exception;
        }
        // not exception, cache bind info
        EasyMqttCacheFactory.bindSubscribe(clientId, subscribeInfo);
    }

    /**
     * Method Description: subscribe internal
     * <p> only support: publish with response</p>
     *
     * @param clientId mqtt client id
     * @param topic    subscribe topic
     * @param qos      qos
     * @param consumer message consumer
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:51
     */
    static void subscribe(String clientId, String topic, int qos, Consumer<Message> consumer) {
        // get mqtt client from cache
        ClientInstance<?> instance = EasyMqttCacheFactory.getClient(clientId);

        if (instance instanceof AsyncClientInstance asyncClientInstance) {
            try {
                asyncClientInstance.getMqttClient().subscribe(topic, qos, (tp, msg) -> consumer.accept(Message.ofMqttMessage(tp, msg)));
                LOGGER.info("Mqtt Async Client [" + clientId + "] subscribe [" + topic + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else if (instance instanceof GenericClientInstance genericClientInstance) {
            try {
                genericClientInstance.getMqttClient().subscribe(topic, qos, (tp, msg) -> consumer.accept(Message.ofMqttMessage(tp, msg)));
                LOGGER.info("Mqtt Client [" + clientId + "] subscribe [" + topic + "]");
            } catch (MqttException e) {
                throw new EasyMqttException(e.getMessage());
            }
        } else {
            throw new EasyMqttException("Unsupported mqtt client instance type: " + instance.getClass());
        }
    }

    /**
     * Method Description: disconnected mqtt client
     *
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:31
     */
    public static void disconnected() {
        EasyMqttCacheFactory.getAllClient().forEach((clientId, o) -> {
            // disconnected
            if (o instanceof AsyncClientInstance asyncClientInstance) {
                if (asyncClientInstance.getMqttClient().isConnected()) {
                    try {
                        asyncClientInstance.getMqttClient().disconnect();
                    } catch (Exception ignored) {
                    }
                }
            }

            // disconnected
            if (o instanceof GenericClientInstance genericClientInstance) {
                if (genericClientInstance.getMqttClient().isConnected()) {
                    try {
                        genericClientInstance.getMqttClient().disconnect();
                    } catch (Exception ignored) {
                    }
                }
            }

            // clean cache client
            EasyMqttCacheFactory.cleanClient(clientId);
        });
    }

    /***
     * Method Description: get subscribe message handler
     *
     * @param clientId mqtt client id
     * @param topic    topic
     * @return io.easymqtt.handle.MqttMessageHandler
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 15:36
     */
    static List<MqttMessageHandler> getMqttMessageHandlers(ClientId clientId, String topic) {
        Map<String, Set<SubscribeInfo>> subscribeInfoMap = EasyMqttCacheFactory.getSubscribeByClientId(clientId.clientId());
        if (subscribeInfoMap.isEmpty()) {
            return null;
        }
        return subscribeInfoMap.entrySet().stream()
                .filter(entry -> TopicUtil.isMatched(entry.getKey(), topic))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
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
        try {
            EasyMqttCacheFactory.getClient(clientId);
            return clientId;
        } catch (EasyMqttException e) {
            return null;
        }
    }

    /**
     * Method Description:
     *
     * @param clientId mqtt client id
     * @param topic    unsubscribe topic
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/25 18:26
     */
    public static void unsubscribe(String clientId, String topic) {
        try {
            ClientInstance<?> object = EasyMqttCacheFactory.getClient(clientId);
            LOGGER.info("Mqtt client " + clientId + " unsubscribe [" + topic + "]");
            if (object instanceof GenericClientInstance genericClientInstance) {
                genericClientInstance.getMqttClient().unsubscribe(topic);
            }
            if (object instanceof AsyncClientInstance asyncClientInstance) {
                asyncClientInstance.getMqttClient().unsubscribe(topic);
            }
            // clean cache subscribe
            EasyMqttCacheFactory.unbindSubscribe(clientId, topic);
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
        try {
            ClientInstance<?> object = EasyMqttCacheFactory.getClient(message.clientId());
            if (object instanceof GenericClientInstance genericClientInstance) {
                genericClientInstance.getMqttClient().publish(message.topic(), message.payload(), message.qos(), message.isRetained());
            }

            if (object instanceof AsyncClientInstance asyncClientInstance) {
                asyncClientInstance.getMqttClient().publish(message.topic(), message.payload(), message.qos(), message.isRetained());
            }
        } catch (Exception e) {
            throw new EasyMqttException(e.getMessage());
        }
    }

    /**
     * Method Description: valid open auto reconnect
     *
     * @param clientId mqtt client id
     * @return boolean
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 17:34
     */
    public static boolean validationOpenAutoReconnect(String clientId) {
        return EasyMqttCacheFactory.allowAutoReconnected(clientId);
    }

}
