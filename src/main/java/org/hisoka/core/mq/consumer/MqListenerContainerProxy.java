package org.hisoka.core.mq.consumer;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class MqListenerContainerProxy {


    /**
     * 目前全局broker统一管理, 暂不支持动态化
     */
    private String mqListenerKey = "mq";

    private String queueNames;

    private SimpleMessageListenerContainer messageListenerContainer;

    private boolean autoStartup = true;

    public String getMqListenerKey() {
        return mqListenerKey;
    }

    public String getQueueNames() {
        return queueNames;
    }

    public void setQueueNames(String queueNames) {
        this.queueNames = queueNames;
    }

    public SimpleMessageListenerContainer getMessageListenerContainer() {
        return messageListenerContainer;
    }

    public void setMessageListenerContainer(SimpleMessageListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }

    public boolean getAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public String toString() {
        return "MqListenerContainerProxy [mqListenerKey=" + mqListenerKey + ", queueNames=" + queueNames + ", messageListenerContainer="
                + messageListenerContainer + ", autoStartup=" + autoStartup + "]";
    }
}
