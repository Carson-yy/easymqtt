/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.annotations;

import java.lang.annotation.*;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className QueueMqttListener
 * @description
 * @date 2024/8/24 19:37
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueueMqttListener {

    /**
     * Method Description: mqtt client id, support SpEL
     *
     * @return java.lang.String
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:31
     */
    String clientId();

    /**
     * Method Description: is async client, default false
     *
     * @return boolean
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/24 19:21
     */
    boolean async() default false;

    /**
     * Method Description: listen topics
     *
     * @return java.lang.String[]
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:31
     */
    Topic[] topics();
}
