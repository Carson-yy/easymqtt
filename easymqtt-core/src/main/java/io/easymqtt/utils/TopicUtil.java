/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.utils;

import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className TopicUtil
 * @description
 * @date 2024/8/24 15:39
 */
public final class TopicUtil {

    /**
     * topic matched
     */
    public static boolean isMatched(String filterTopic, String topicName) {
        if(filterTopic.startsWith("$share")) {
            filterTopic = filterTopic.replaceAll("^\\$share/[^/]+/", "");
        }
        if(filterTopic.startsWith("$queue")) {
            filterTopic = filterTopic.replace("$queue/", "");
        }
        return MqttTopic.isMatched(filterTopic, topicName);
    }

}
