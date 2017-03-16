package org.hisoka.core.mq.apply;

import org.hisoka.core.context.SpringContextHolder;
import org.hisoka.core.mq.consumer.MqListenerContainerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DynamicCreateMqListenerManager {

    private DynamicMqListenerContainer dynamicMqListenerContainer;

    private List<MqListenerContainerProxy> mqListenerContainerProxyList;

    public void setDynamicMqListenerContainer(DynamicMqListenerContainer dynamicMqListenerContainer) {
        this.dynamicMqListenerContainer = dynamicMqListenerContainer;
    }

    public void setMqListenerContainerProxyList(List<MqListenerContainerProxy> mqListenerContainerProxyList) {
        this.mqListenerContainerProxyList = mqListenerContainerProxyList;
    }

    /**
     * 初始化mq
     *
     */
    public void initCreateMqListener() {
        registerMqListener();
    }

    /**
     *
     */
    private void registerMqListener() {
        Map<String, MqListenerContainerProxy> targetMqListenerContainerProxyMap = new HashMap<String, MqListenerContainerProxy>();
        List<MqListenerContainerProxy> mqListenerContainerProxyList = new ArrayList<MqListenerContainerProxy>();

        if (this.mqListenerContainerProxyList == null || this.mqListenerContainerProxyList.isEmpty()) {
            Map<String, MqListenerContainerProxy> mqListenerContainerProxyMap = SpringContextHolder.applicationContext
                    .getBeansOfType(MqListenerContainerProxy.class);

            if (mqListenerContainerProxyMap != null && !mqListenerContainerProxyMap.isEmpty()) {
                for (Entry<String, MqListenerContainerProxy> en : mqListenerContainerProxyMap.entrySet()) {
                    mqListenerContainerProxyList.add(en.getValue());
                }
            }
        } else {
            mqListenerContainerProxyList = this.mqListenerContainerProxyList;
        }

        for (MqListenerContainerProxy mqListenerContainerProxy : mqListenerContainerProxyList) {
            String mqListenerKey = mqListenerContainerProxy.getMqListenerKey();
            targetMqListenerContainerProxyMap.put(mqListenerKey, mqListenerContainerProxy);
        }

        dynamicMqListenerContainer.setTargetMqListenerContainerProxyMap(targetMqListenerContainerProxyMap);
        dynamicMqListenerContainer.initMqLog();
        dynamicMqListenerContainer.afterPropertiesSet();
    }

}
