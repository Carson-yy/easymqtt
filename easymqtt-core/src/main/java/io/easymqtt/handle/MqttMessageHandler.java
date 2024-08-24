/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.handle;

import io.easymqtt.domain.Message;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className MqttMessageHandler
 * @description
 * @date 2024/8/22 21:11
 */
public interface MqttMessageHandler {

    /**
     * Method Description:
     *
     * @param message 消息
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 21:11
     */
    void handle(Message message);

}
