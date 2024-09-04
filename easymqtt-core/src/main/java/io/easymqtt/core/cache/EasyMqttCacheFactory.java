/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.core.cache;

import io.easymqtt.domain.SubscribeInfo;
import io.easymqtt.domain.instance.ClientInstance;
import io.easymqtt.exceptions.EasyMqttException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className EasyMqttCacheFactory
 * @description 缓存
 * @date 2024/9/4 15:54
 */
public final class EasyMqttCacheFactory {

    private EasyMqttCacheFactory() {

    }

    /**
     * mqtt client map
     */
    private static final ConcurrentHashMap<String, ClientInstance<?>> CLIENTS = new ConcurrentHashMap<>(16);


    /**
     * CLIENT_AUTO_RECONNECT
     */
    private static final Set<String> CLIENT_AUTO_RECONNECT = new HashSet<>();

    /**
     * mqtt client bind subscribe topic
     */
    private static final ConcurrentHashMap<String, Set<String>> CLIENT_TOPIC_BIND = new ConcurrentHashMap<>(16);


    /**
     * client bind subscribe info
     */
    private static final ConcurrentHashMap<String, Set<SubscribeInfo>> CLIENT_SUBSCRIBE_BIND = new ConcurrentHashMap<>(16);

    /**
     * Method Description: add cache
     *
     * @param clientInstances mqtt client instance
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 15:57
     */
    public static void addClient(ClientInstance<?> clientInstances) {
        CLIENTS.put(clientInstances.getClientId().clientId(), clientInstances);

        // cache auto reconnect client
        if(Objects.nonNull(clientInstances.getConfig().automaticReconnect())? clientInstances.getConfig().automaticReconnect(): Boolean.TRUE){
            CLIENT_AUTO_RECONNECT.add(clientInstances.getClientId().clientId());
        }
    }

    /**
     * Method Description:
     *
     * @param clientId mqtt client id
     * @return io.easymqtt.domain.instance.ClientInstance<?>
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 16:18
     */
    public static ClientInstance<?> getClient(String clientId) {
        ClientInstance<?> clientInstance = CLIENTS.get(clientId);
        Optional.ofNullable(clientInstance).orElseThrow(() -> new EasyMqttException("Client is not found, "+ clientId));
        return clientInstance;
    }

    /**
     * Method Description: client bind subscribe
     *
     * @param clientId mqtt client id
     * @param subscribeInfo subscribe info
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 16:38
     */
    public static void bindSubscribe(String clientId, SubscribeInfo subscribeInfo) {
        // get mqtt client bind subscribe topics
        Set<String> topics = CLIENT_TOPIC_BIND.get(clientId);

        // not found client id, init empty set
        if(Objects.isNull(topics)){
            CLIENT_TOPIC_BIND.put(clientId, new HashSet<>());
        }

        // save client bind topic
        CLIENT_TOPIC_BIND.get(clientId).add(subscribeInfo.topic());

        // key id
        String id = clientId + "-" + subscribeInfo.topic();

        // get subscribe info
        Set<SubscribeInfo> subscribeInfoSet = CLIENT_SUBSCRIBE_BIND.get(id);

        // init empty set
        if(Objects.isNull(subscribeInfoSet)){
            CLIENT_SUBSCRIBE_BIND.put(id, new HashSet<>());
        }

        // add bind subscribe
        CLIENT_SUBSCRIBE_BIND.get(id).add(subscribeInfo);
    }

    /**
     * Method Description: get all client
     *
     * @return java.util.concurrent.ConcurrentHashMap<java.lang.String,io.easymqtt.domain.instance.ClientInstance<?>>
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 16:46
     */
    public static ConcurrentHashMap<String, ClientInstance<?>> getAllClient() {
        return CLIENTS;
    }


    /**
     * Method Description: clean client
     *
     * @param clientId mqtt client id
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 16:51
     */
    public static void cleanClient(String clientId) {
        cleanClientAllBind(clientId);
        CLIENT_AUTO_RECONNECT.remove(clientId);
        CLIENTS.remove(clientId);
    }

    /**
     * Method Description: get subscribe for client
     *
     * @param clientId mqtt client id
     * @return java.util.Map<java.lang.String,java.util.Set<io.easymqtt.domain.SubscribeInfo>>
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 16:55
     */
    public static Map<String, Set<SubscribeInfo>> getSubscribeByClientId(String clientId) {
        Map<String, Set<SubscribeInfo>> subscribeMap = new HashMap<>();
        Set<String> topics = CLIENT_TOPIC_BIND.get(clientId);
        if(Objects.isNull(topics)){
            return subscribeMap;
        }
        topics.forEach(topic -> {
            String id = clientId + "-" + topic;
            Set<SubscribeInfo> subscribeInfoSet = CLIENT_SUBSCRIBE_BIND.getOrDefault(id, new HashSet<>());
            subscribeMap.put(topic, subscribeInfoSet);
        });

        return subscribeMap;

    }

    /**
     * Method Description: unbind subscribe to client
     *
     * @param clientId mqtt client id
     * @param topic topic
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 17:31
     */
    public static void unbindSubscribe(String clientId, String topic) {
        Set<String> topics = CLIENT_TOPIC_BIND.get(clientId);
        if(Objects.isNull(topics)){
            return;
        }
        topics.remove(topic);
        CLIENT_SUBSCRIBE_BIND.remove(clientId + "-" + topic);
    }

    /**
     * Method Description: auto reconnect allow
     *
     * @param clientId client id
     * @return boolean
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 17:35
     */
    public static boolean allowAutoReconnected(String clientId) {
        return CLIENT_AUTO_RECONNECT.contains(clientId);
    }

    /**
     * Method Description:  clean All bind by client id
     *
     * @param clientId mqtt client id
     * @author Carson yangbaopan@gmail.com
     * @date 2024/9/4 17:47
     */
    public static void cleanClientAllBind(String clientId) {
        CLIENT_TOPIC_BIND.getOrDefault(clientId, new HashSet<>()).stream().map(topic -> clientId + "-" + topic).forEach(CLIENT_SUBSCRIBE_BIND::remove);
        CLIENT_TOPIC_BIND.remove(clientId);
    }
}
