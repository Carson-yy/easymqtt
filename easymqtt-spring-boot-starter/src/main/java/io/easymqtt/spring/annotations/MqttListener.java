/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.annotations;

import java.lang.annotation.*;

/**
 * Project Name: easymqtt
 *
 * @author BaoPan.Yang baopan.yang@dyness-tech.com
 * @className MqttListener
 * @description mqtt message listener
 * @date 2024/8/19 20:26
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqttListener {

    /**
     * Method Description: mqtt client id, support SpEL
     *
     * @return java.lang.String
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:31
     */
    String clientId();

    /**
     * Method Description: listen topics
     *
     * @return java.lang.String[]
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:31
     */
    Topic[] topics();
}
