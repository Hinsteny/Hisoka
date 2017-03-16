package org.hisoka.rpc.dubbo.config;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DubboConfigServer {

    /**
     * 目前全局zk统一管理，暂不支持动态化
     */
    private String configServerKey = "dubbo";

    private String applicationName;

    private String registryAddress;

    private String registryUsername;

    private String registryPassword;

    private String registryFile;

    private boolean isDefault;

    public String getConfigServerKey() {
        return configServerKey;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryUsername() {
        return registryUsername;
    }

    public void setRegistryUsername(String registryUsername) {
        this.registryUsername = registryUsername;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public void setRegistryPassword(String registryPassword) {
        this.registryPassword = registryPassword;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getRegistryFile() {
        return registryFile;
    }

    public void setRegistryFile(String registryFile) {
        this.registryFile = registryFile;
    }

    @Override
    public String toString() {
        return "DubboConfigServer [configServerKey=" + configServerKey + ", applicationName=" + applicationName + ", registryAddress=" + registryAddress
                + ", registryUsername=" + registryUsername + ", registryPassword=" + registryPassword + ", registryFile=" + registryFile + ", isDefault="
                + isDefault + "]";
    }

}
