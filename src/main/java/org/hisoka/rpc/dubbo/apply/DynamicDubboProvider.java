package org.hisoka.rpc.dubbo.apply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisoka.common.util.string.StringUtil;
import org.hisoka.rpc.dubbo.config.DubboConfigServer;
import org.hisoka.rpc.dubbo.extension.DubboExtensionLoader;
import org.hisoka.rpc.dubbo.provider.DubboService;
import org.hisoka.rpc.dubbo.provider.DubboServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DynamicDubboProvider {


    /**
     * 日志
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private DubboConfigServer defaultTargetDubboConfigServer;

    private List<DubboService> targetDubboServiceList;

    private Map<String, List<ServiceConfig<?>>> serviceConfigMap;

    private final static Integer PROTOCOL_THREADS = 100;

    /**
     * 日志开关，默认为false不打开
     */
    private boolean openLog = false;

    public Map<String, List<ServiceConfig<?>>> getServiceConfigMap() {
        return serviceConfigMap;
    }

    public void setDefaultTargetDubboConfigServer(DubboConfigServer defaultTargetDubboConfigServer) {
        this.defaultTargetDubboConfigServer = defaultTargetDubboConfigServer;
    }

    public void setTargetDubboServiceList(List<DubboService> targetDubboServiceList) {
        this.targetDubboServiceList = targetDubboServiceList;
    }

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void afterPropertiesSet() {
        DubboExtensionLoader.loadExtension();
        serviceConfigMap = new HashMap<String, List<ServiceConfig<?>>>();

        for (DubboService dubboService : targetDubboServiceList) {
            DubboConfigServer dubboConfigServer = dubboService.getDubboConfigServer();

            if (dubboConfigServer == null) {
                dubboConfigServer = defaultTargetDubboConfigServer;
            }

            String configServerKey = dubboConfigServer.getConfigServerKey();

            // 当前应用配置
            ApplicationConfig application = new ApplicationConfig();
            application.setName(dubboConfigServer.getApplicationName());

            // 连接注册中心配置
            RegistryConfig registry = new RegistryConfig();
            registry.setAddress(dubboConfigServer.getRegistryAddress());
            registry.setUsername(dubboConfigServer.getRegistryUsername());
            registry.setPassword(dubboConfigServer.getRegistryPassword());

            if (StringUtil.isNotBlank(dubboConfigServer.getRegistryFile())) {
                registry.setFile(dubboConfigServer.getRegistryFile());
            }

            // 服务提供者协议配置
            List<ProtocolConfig> protocolList = new ArrayList<ProtocolConfig>();
            String[] protocolConfigs = dubboService.getProtocols().split(",");

            for (String protocolConfig : protocolConfigs) {
                String protocolName = protocolConfig.split(":")[0];
                int port = Integer.parseInt(protocolConfig.split(":")[1]);
                ProtocolConfig protocol = new ProtocolConfig();
                protocol.setName(protocolName);
                protocol.setPort(port);
                protocol.setThreads(PROTOCOL_THREADS);
                protocolList.add(protocol);
            }

            // 动态关联
            List<ServiceConfig<?>> serviceConfigList = serviceConfigMap.get(configServerKey);

            if (serviceConfigList == null) {
                serviceConfigList = new ArrayList<ServiceConfig<?>>();
            }

            // 服务提供者
            ServiceConfig<Object> service = new ServiceConfig<Object>();
            service.setApplication(application);
            service.setRegistry(registry);
            service.setProtocols(protocolList);
            service.setInterface(dubboService.getInterfaceClass());
            service.setRef(dubboService.getInterfaceRef());
            service.setVersion(dubboService.getVersion());
            service.setOwner(dubboService.getOwner());
            service.setTimeout(dubboService.getTimeout());
            service.setRetries(dubboService.getRetries());
            service.setProxy(DubboServiceFactory.EXTENSION_NAME);
            serviceConfigList.add(service);
            startService(configServerKey, dubboService, service);
            serviceConfigMap.put(configServerKey, serviceConfigList);
        }
    }

    public List<ServiceConfig<?>> getServiceConfigList(String dubboServiceKey) {
        if (serviceConfigMap == null) {
            return null;
        }

        return serviceConfigMap.get(dubboServiceKey);
    }

    public void initDubboLog() {
        DubboServiceFactory.setOpenLog(openLog);
    }

    private void startService(String configServerKey, DubboService dubboService, ServiceConfig<?> service) {
        service.export();
        log.info("Dubbo configServer " + configServerKey + ", service[name= " + dubboService.getInterfaceName() + ", version= " + dubboService.getVersion()
                + " ] 成功启动");
    }

}
