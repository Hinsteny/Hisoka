package org.hisoka.rpc.dubbo.registry;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.zookeeper.ZookeeperRegistryFactory;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.hisoka.common.util.reflect.ReflectUtil;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DuZookeeperRegistryFactory extends ZookeeperRegistryFactory {

    public static final String EXTENSION_NAME = "zookeeper";

    @Override
    public Registry createRegistry(URL url) {
        ZookeeperTransporter zookeeperTransporter =  (ZookeeperTransporter) ReflectUtil.getFieldValue(this, "zookeeperTransporter");
        return new DuZookeeperRegistry(url, zookeeperTransporter);
    }
}