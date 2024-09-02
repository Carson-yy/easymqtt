package io.easymqtt.monitor;


import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Project Name: easymqtt
 *
 * @author yangbaopan yangbaopan@gmail.com
 * @className EasyMqttClientMonitor
 * @description mqtt client monitor
 * @date 2024/09/02 20:21:55
 */
public final class EasyMqttClientMonitor {

    /**
     * SCHEDULED_EXECUTOR
     */    
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR = new ScheduledThreadPoolExecutor(1);

    /**
     * FUTURES
     */    
    private final static ConcurrentHashMap<String, ScheduledFuture<?>> SCHEDULEDS = new ConcurrentHashMap<>(16);

    private EasyMqttClientMonitor() {

    }

    /**
     * Method Description: add Reconnect Client
     *
     * @param clientId clientId
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 20:46:53
     */    
    public static void addReconnectClient(String clientId) {
        ScheduledFuture<?> scheduledFuture =  SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> new ReconnectTask(clientId), 1000L, 1000L, TimeUnit.SECONDS);
        SCHEDULEDS.put(clientId, scheduledFuture); 
    }
    
    /**
     * Method Description: cancel Scheduled
     *
     * @param clientId clientId
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/02 20:49:40
     */    
    public static void cancelScheduled(String clientId) {
        ScheduledFuture<?> scheduledFuture = SCHEDULEDS.get(clientId);
        if(Objects.nonNull(scheduledFuture) && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
    }
}
