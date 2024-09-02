package io.easymqtt.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import io.easymqtt.core.MqttClientContainer;
import io.easymqtt.domain.SubscribeInfo;
import io.easymqtt.domain.instance.AsyncClientInstance;
import io.easymqtt.domain.instance.ClientInstance;
import io.easymqtt.domain.instance.GenericClientInstance;

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
     * mqtt client id
     */
    private String clientId;

    /**
     * mqtt client
     */
    private Object mqttClient;

    /**
     * subscribes
     */
    private Map<String, List<SubscribeInfo>> subscribes;

    /**
     * Method Description: Create New Instance
     *
     * @param clientId mqtt client id
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 20:42:09
     */
    public ReconnectTask(String clientId) {
        this.clientId = clientId;
        this.mqttClient = getMqttClient();
        this.subscribes = MqttClientContainer.getSubscribeInfo(clientId);
    }

    /**
     * Method Description: get Mqtt Client
     *
     * @return org.eclipse.paho.client.mqttv3.MqttClient
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 20:50:49
     */
    private Object getMqttClient() {
        Object object = MqttClientContainer.getClient(this.clientId);
        if (Objects.nonNull(object) && object instanceof ClientInstance clientInstance) {
            return clientInstance.getMqttClient();
        }
        return null;
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
        // connected
        // clean cache
        subscribes.keySet().forEach(topic -> MqttClientContainer.cleanSubscribeInfo(this.clientId, topic));

        // resubscribe
        List<SubscribeInfo> subscribeInfoList = subscribes.entrySet().stream().map(Entry::getValue)
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        subscribeInfoList.forEach(subscribeInfo -> MqttClientContainer.subscribe(this.clientId, subscribeInfo));

        // cancel task
        EasyMqttClientMonitor.cancelScheduled(this.clientId);
    }
}
