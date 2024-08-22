/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.spring.annotations;

import java.lang.annotation.*;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className Topic
 * @description
 * @date 2024/8/22 22:14
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Topic {

    /**
     * Method Description: topic
     *
     * @return java.lang.String
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:15
     */
    String topic();

    /**
     * Method Description: qos
     *
     * @return int
     * @author Carson yangbaopan@gmail.com
     * @date 2024/8/22 22:15
     */
    int qos() default 0;
}
