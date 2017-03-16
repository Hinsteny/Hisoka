package org.hisoka.core.mq.apply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hisoka.core.context.SpringContextHolder;
import org.hisoka.core.mq.producer.RabbitTemplateProxy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DynamicCreateMqProducerManager {

    private DynamicMqMessageSender dynamicMqMessageSender;

    private List<RabbitTemplateProxy> rabbitTemplateProxyList;

    public void setRabbitTemplateProxyList(List<RabbitTemplateProxy> rabbitTemplateProxyList) {
        this.rabbitTemplateProxyList = rabbitTemplateProxyList;
    }

    public void setDynamicMqMessageSender(DynamicMqMessageSender dynamicMqMessageSender) {
        this.dynamicMqMessageSender = dynamicMqMessageSender;
    }

    /**
     * 初始化mq
     *
     */
    public void initCreateMqProducer() {
        registerMqProducer();
    }

    /**
     *
     */
    private void registerMqProducer() {
        Map<String, RabbitTemplate> targetRabbitTemplates = new HashMap<String, RabbitTemplate>();
        List<RabbitTemplateProxy> rabbitTemplateProxyList = new ArrayList<RabbitTemplateProxy>();
        RabbitTemplate defaultTargetRabbitTemplate = null;

        if (this.rabbitTemplateProxyList == null || this.rabbitTemplateProxyList.isEmpty()) {
            Map<String, RabbitTemplateProxy> rabbitTemplateProxyMap = SpringContextHolder.applicationContext.getBeansOfType(RabbitTemplateProxy.class);

            if (rabbitTemplateProxyMap != null && !rabbitTemplateProxyMap.isEmpty()) {
                for (Entry<String, RabbitTemplateProxy> en : rabbitTemplateProxyMap.entrySet()) {
                    rabbitTemplateProxyList.add(en.getValue());
                }
            }
        } else {
            rabbitTemplateProxyList = this.rabbitTemplateProxyList;
        }

        for (RabbitTemplateProxy rabbitTemplateProxy : rabbitTemplateProxyList) {
            String mqProducerKey = rabbitTemplateProxy.getMqProducerKey();
            RabbitTemplate rabbitTemplate = rabbitTemplateProxy.getRabbitTemplate();
            targetRabbitTemplates.put(mqProducerKey, rabbitTemplate);
            boolean isDefault = rabbitTemplateProxy.getIsDefault();

            if (isDefault) {
                defaultTargetRabbitTemplate = rabbitTemplate;
            }
        }

        dynamicMqMessageSender.setTargetRabbitTemplates(targetRabbitTemplates);
        dynamicMqMessageSender.setDefaultTargetRabbitTemplate(defaultTargetRabbitTemplate);
        dynamicMqMessageSender.initMqLog();
        dynamicMqMessageSender.afterPropertiesSet();
    }
}
