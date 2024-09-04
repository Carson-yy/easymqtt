package io.easymqtt.monitor;

import java.util.*;
import java.util.logging.Logger;

import io.easymqtt.core.MqttClientContainer;
import io.easymqtt.core.cache.EasyMqttCacheFactory;
import io.easymqtt.domain.SubscribeInfo;
import io.easymqtt.domain.instance.AsyncClientInstance;
import io.easymqtt.domain.instance.GenericClientInstance;
import io.easymqtt.exceptions.EasyMqttException;

/**
 * Project Name: easymqtt
 *
 * @author yangbaopan yangbaopan@gmail.com
 * @className ReconnectTask
 * @description mqtt client reconnect task
 * @date 2024/09/02 20:40:02
 */
public class ReconnectTask implements Runnable {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = Logger.getLogger(ReconnectTask.class.getName());

    /**
     * mqtt client id
     */
    private final String clientId;

    /**
     * mqtt client
     */
    private Object mqttClient;

    /**
     * subscribes
     */
    private final Map<String, Set<SubscribeInfo>> subscribes;

    /**
     * Method Description: Create New Instance
     *
     * @param clientId mqtt client id
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 20:42:09
     */
    public ReconnectTask(String clientId) {
        this.clientId = clientId;
        try {
            this.mqttClient = EasyMqttCacheFactory.getClient(clientId);
        } catch (EasyMqttException e) {
            LOGGER.severe(e.getMessage());
        }
        this.subscribes = EasyMqttCacheFactory.getSubscribeByClientId(clientId);
    }

    /**
     * Method Description: run
     *
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 21:07:38
     */
    @Override
    public void run() {
        if (Objects.nonNull(mqttClient) && Objects.nonNull(subscribes) && !subscribes.isEmpty()) {
            if (mqttClient instanceof GenericClientInstance genericClientInstance) {
                if (genericClientInstance.getMqttClient().isConnected()) {
                    reconnect();
                }
            } else if(mqttClient instanceof AsyncClientInstance asyncClientInstance) {
                if(asyncClientInstance.getMqttClient().isConnected()) {
                    reconnect();
                }
            }
        } else {
            LOGGER.info("MQTT client is not found or subscribe is empty, cancel reconnect task!");
            // mqtt client not exist，cancel task
            EasyMqttClientMonitor.cancelScheduled(this.clientId);
        }
    }

    /**
     * Method Description: reconnect
     *
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 21:08:40
     */    
    private void reconnect() {
        LOGGER.info("MQTT client reconnect successfully, start resubscribes!");
        // connected
        // clean cache
        EasyMqttCacheFactory.cleanClientAllBind(this.clientId);

        // resubscribe
        List<SubscribeInfo> subscribeInfoList = subscribes.values().stream()
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        subscribeInfoList.forEach(subscribeInfo -> MqttClientContainer.subscribe(this.clientId, subscribeInfo));

        // cancel task
        EasyMqttClientMonitor.cancelScheduled(this.clientId);
    }
}
