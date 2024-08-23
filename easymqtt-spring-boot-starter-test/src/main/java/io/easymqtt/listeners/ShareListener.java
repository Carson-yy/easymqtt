/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.listeners;

import io.easymqtt.domain.Message;
import io.easymqtt.spring.annotations.MqttListener;
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


    @MqttListener(clientId = "${easymqtt.clients[0].id}", topics = {
            @Topic(topic = "$share/dev/device/#", qos = 0),
            @Topic(topic = "$share/dev/$SYS/#", qos = 2)
    })
    public void subscribeDemo(Message message) {
        System.out.println(message);
    }


}
