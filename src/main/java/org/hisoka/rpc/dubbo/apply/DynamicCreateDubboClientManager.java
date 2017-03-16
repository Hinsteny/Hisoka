package org.hisoka.rpc.dubbo.apply;

import org.hisoka.core.context.SpringContextHolder;
import org.hisoka.rpc.dubbo.client.DubboClient;
import org.hisoka.rpc.dubbo.config.DubboConfigServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DynamicCreateDubboClientManager {


    private DynamicDubboClient dynamicDubboClient;

    private List<DubboClient> dubboClientList;

    private List<DubboConfigServer> dubboConfigServerList;

    public void setDynamicDubboClient(DynamicDubboClient dynamicDubboClient) {
        this.dynamicDubboClient = dynamicDubboClient;
    }

    public void setDubboClientList(List<DubboClient> dubboClientList) {
        this.dubboClientList = dubboClientList;
    }

    public void setDubboConfigServerList(List<DubboConfigServer> dubboConfigServerList) {
        this.dubboConfigServerList = dubboConfigServerList;
    }

    /**
     * 初始化dubbo的服务消费者
     *
     */
    public void initCreateDubboClient() {
        registerDubboClient();
    }

    /**
     *
     *
     */
    private void registerDubboClient() {
        List<DubboClient> dubboClientList = new ArrayList<DubboClient>();
        List<DubboConfigServer> dubboConfigServerList = new ArrayList<DubboConfigServer>();
        DubboConfigServer defaultTargetDubboConfigServer = null;

        if (this.dubboClientList == null || this.dubboClientList.isEmpty()) {
            Map<String, DubboClient> dubboClientMap = SpringContextHolder.applicationContext.getBeansOfType(DubboClient.class);

            if (dubboClientMap != null && !dubboClientMap.isEmpty()) {
                for (Entry<String, DubboClient> en : dubboClientMap.entrySet()) {
                    dubboClientList.add(en.getValue());
                }
            }
        } else {
            dubboClientList = this.dubboClientList;
        }

        if (this.dubboConfigServerList == null || this.dubboConfigServerList.isEmpty()) {
            Map<String, DubboConfigServer> dubboConfigServerMap = SpringContextHolder.applicationContext.getBeansOfType(DubboConfigServer.class);

            if (dubboConfigServerMap != null && !dubboConfigServerMap.isEmpty()) {
                for (Entry<String, DubboConfigServer> en : dubboConfigServerMap.entrySet()) {
                    dubboConfigServerList.add(en.getValue());
                }
            }
        } else {
            dubboConfigServerList = this.dubboConfigServerList;
        }

        for (DubboConfigServer dubboConfigServer : dubboConfigServerList) {
            boolean isDefault = dubboConfigServer.getIsDefault();

            if (isDefault) {
                defaultTargetDubboConfigServer = dubboConfigServer;
            }
        }

        dynamicDubboClient.setTargetDubboClientList(dubboClientList);
        dynamicDubboClient.setDefaultTargetDubboConfigServer(defaultTargetDubboConfigServer);
        dynamicDubboClient.initDubboLog();
        dynamicDubboClient.afterPropertiesSet();
    }

}
