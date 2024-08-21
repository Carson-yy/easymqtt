/**
 * Copyright © 2024 Carson. All Right Reserved.
 */
package io.easymqtt.domain;

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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ClientId that){
            return that.clientId.equals(this.clientId);
        }
        return false;
    }
}
