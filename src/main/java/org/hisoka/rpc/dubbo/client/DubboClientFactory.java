package org.hisoka.rpc.dubbo.client;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public interface DubboClientFactory {

    /**
     * 获取某个dubbo消费端
     *
     * @param beanId dubbo客户端对应的beanId
     * @return dubbo客户端
     */
    <T> T getDubboClient(String beanId);

    /**
     * 获取某个dubbo消费端
     *
     * @param dubboClient dubbo客户端对应的bean
     * @return dubbo客户端
     */
    <T> T getDubboClient(DubboClient dubboClient);
}
