/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.annotations;

import java.lang.annotation.*;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className AsyncMqttListener
 * @description 异步注解
 * @date 2024/8/21 20:29
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncMqttListener {

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
     * @return java.lang.String
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/19 20:31
     */
    String topics();
}
