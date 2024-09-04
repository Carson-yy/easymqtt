package io.easymqtt.monitor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project Name: easymqtt
 *
 * @author yangbaopan yangbaopan@gmail.com
 * @className ReconnectThreadFactory
 * @description mqtt client reconnect thread factory 
 * @date 2024/09/04 21:19:39
 */
public class ReconnectThreadFactory implements ThreadFactory {

    /**
     * thread group
     */
    private final ThreadGroup group;

    /**
     * thread number
     */
    private final AtomicInteger threadNumber = new AtomicInteger(0);

    /**
     * thread name prefix
     */
    private final String namePrefix;

    /**
     * Method Description: Create New Instance
     *
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/04 21:21:42
     */    
    public ReconnectThreadFactory() {
        group = Thread.currentThread().getThreadGroup();
        namePrefix = "MQTT Reconnect Thread-";
    }

    /**
     * Method Description: new Thread
     *
     * @param r r
     * @return java.lang.Thread
     * @author yangbaopan yangbaopan@gmail.com
     * @date 2024/09/04 21:21:45
     */    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, this.namePrefix + threadNumber.getAndIncrement(), 0);
        if(t.isDaemon()) {
            t.setDaemon(false);
        }
        if(t.getPriority() !=Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
    
}
