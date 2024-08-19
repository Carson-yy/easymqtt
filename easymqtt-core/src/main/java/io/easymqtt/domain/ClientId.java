/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

import java.io.Serializable;

/**
 * Project Name: easymqtt
 *
 * @author Carson yangbaopan@gmail.com
 * @className ClientId
 * @description
 * @date 2024/8/19 20:50
 */

public record ClientId(
        // mqtt client id
        String clientId,
        // real mqtt client id
        String realClientId
) {

}
