package org.hisoka.core.mq.apply;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hisoka.core.mq.consumer.MqListenerContainerProxy;
import org.hisoka.core.mq.consumer.MqMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DynamicMqListenerContainer {

    /**
     * 日志
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, MqListenerContainerProxy> targetMqListenerContainerProxy;

    /**
     * 日志开关，默认为false不打开
     */
    private boolean openLog = false;

    /**
     * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
     */
    private int logLength;

    public void setTargetMqListenerContainerProxyMap(Map<String, MqListenerContainerProxy> targetMqListenerContainerProxy) {
        this.targetMqListenerContainerProxy = targetMqListenerContainerProxy;
    }

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void setLogLength(int logLength) {
        this.logLength = logLength;
    }

    public void afterPropertiesSet() {
        Set<Entry<String, MqListenerContainerProxy>> set = targetMqListenerContainerProxy.entrySet();

        for (Map.Entry<String, MqListenerContainerProxy> entry : set) {
            MqListenerContainerProxy mqListenerContainerProxy = entry.getValue();

            if (mqListenerContainerProxy != null) {
                String[] queueNames = mqListenerContainerProxy.getQueueNames().split(",");
                List<Queue> queues = new ArrayList<Queue>();

                for (String queueName : queueNames) {
                    queues.add(new Queue(queueName, true, false, false));
                }

                SimpleMessageListenerContainer messageListenerContainer = mqListenerContainerProxy.getMessageListenerContainer();
                messageListenerContainer.setQueues(queues.toArray((new Queue[queues.size()])));
                boolean autoStartup = mqListenerContainerProxy.getAutoStartup();
                messageListenerContainer.setAutoStartup(autoStartup);
                startMqListener(mqListenerContainerProxy);
            }
        }
    }

    public void initMqLog() {
        MqMessageListener.setOpenLog(openLog);
        MqMessageListener.setLogLength(logLength);
    }

    private void startMqListener(MqListenerContainerProxy mqListenerContainerProxy) {
        SimpleMessageListenerContainer messageListenerContainer = mqListenerContainerProxy.getMessageListenerContainer();

        if (messageListenerContainer != null) {
            boolean autoStartup = messageListenerContainer.isAutoStartup();

            if (autoStartup) {
                messageListenerContainer.start();
                log.info("mq消费者[" + mqListenerContainerProxy.getMqListenerKey() + "] 自动启动");
            }
        }
    }
}
