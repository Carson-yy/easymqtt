/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.listeners;

import io.easymqtt.domain.Message;
import io.easymqtt.spring.annotations.MqttListener;
import io.easymqtt.spring.annotations.QueueMqttListener;
import io.easymqtt.spring.annotations.ShareMqttListener;
import io.easymqtt.spring.annotations.Topic;
import org.springframework.stereotype.Service;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className ShareListener
 * @description
 * @date 2024/8/23 21:33
 */
@Service
public class ShareListener {


//    @MqttListener(clientId = "${easymqtt.clients[0].id}", topics = {
//            @Topic(topic = "$share/test/device/#", qos = 0)
//    })
//    public void subscribeDemo(Message message) {
//        System.out.println("method subscribeDemo message" + message);
//    }
//
//    @MqttListener(clientId = "${easymqtt.clients[0].id}", topics = {
//            @Topic(topic = "$share/test/device/#", qos = 0)
//    })
//    public void subscribe2Demo(Message message) {
//        System.out.println("method subscribe2Demo message" + message);
//    }
//
//
//    @ShareMqttListener(clientId = "${easymqtt.clients[0].id}",
//            groupId = "test",
//            topics = {
//            @Topic(topic = "device/#", qos = 0)
//    })
//    public void subscribe3Demo(Message message) {
//        System.out.println("method subscribe3Demo message" + message);
//    }

    @QueueMqttListener(clientId = "${easymqtt.clients[0].id}",
            topics = {
                    @Topic(topic = "device/#", qos = 0)
            })
    public void subscribe4Demo(Message message) {
        System.out.println("method subscribe4Demo message" + message);
    }
}
