package org.hisoka.rpc.dubbo.extension;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.registry.RegistryFactory;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.cluster.RouterFactory;
import org.hisoka.rpc.dubbo.client.DubboClientFilter;
import org.hisoka.rpc.dubbo.exception.DubboServiceExceptionFilter;
import org.hisoka.rpc.dubbo.listener.DubboServiceExporterListener;
import org.hisoka.rpc.dubbo.provider.DubboServiceFactory;
import org.hisoka.rpc.dubbo.registry.DuZookeeperRegistryFactory;
import org.hisoka.rpc.dubbo.route.SeConditionRouterFactory;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DubboExtensionLoader {

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    public static void loadExtension() {
        ExtensionLoader registryExtensionLoader = ExtensionLoader.getExtensionLoader(RegistryFactory.class);
        registryExtensionLoader.replaceExtension(DuZookeeperRegistryFactory.EXTENSION_NAME, DuZookeeperRegistryFactory.class);

        ExtensionLoader proxyFactoryExtensionLoader = ExtensionLoader.getExtensionLoader(ProxyFactory.class);
        proxyFactoryExtensionLoader.replaceExtension(DubboServiceFactory.EXTENSION_NAME, DubboServiceFactory.class);

        ExtensionLoader filterExtensionLoader = ExtensionLoader.getExtensionLoader(Filter.class);
        filterExtensionLoader.replaceExtension(DubboClientFilter.EXTENSION_NAME, DubboClientFilter.class);
        filterExtensionLoader.replaceExtension(DubboServiceExceptionFilter.EXTENSION_NAME, DubboServiceExceptionFilter.class);

        ExtensionLoader routerExtensionLoader = ExtensionLoader.getExtensionLoader(RouterFactory.class);
        routerExtensionLoader.replaceExtension(SeConditionRouterFactory.EXTENSION_NAME, SeConditionRouterFactory.class);

        ExtensionLoader exporterExtensionLoader = ExtensionLoader.getExtensionLoader(ExporterListener.class);
        exporterExtensionLoader.replaceExtension(DubboServiceExporterListener.EXTENSION_NAME, DubboServiceExporterListener.class);
    }

}
