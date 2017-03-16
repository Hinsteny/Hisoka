package org.hisoka.common.util.config;

import org.hisoka.common.exception.BusinessException;
import org.hisoka.common.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Hinsteny
 * @Describtion Globle config Util
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class Configuration {

    private static Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static Object lock = new Object();
    private static Configuration config = null;
    private static final String CONFIG_FILE = "app.properties";
    private static Properties props = null;

    private void init(){
        Resource resource = new ClassPathResource(CONFIG_FILE);
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
            if (props == null) {
                throw new BusinessException("Default config file "+CONFIG_FILE+" not found!");
            }else {
                logger.info("Load config file "+CONFIG_FILE+" successed!");
                logger.info("props: {}", props.entrySet().toString());
            }
        } catch (IOException e) {
            throw new BusinessException("Load config file "+CONFIG_FILE+" occurred an error  ", e);
        }
    }

    private Configuration() {
        init();
    }

    public static Configuration getInstance() {
        synchronized(lock) {
            if(null == config) {
                config = new Configuration();
            }
        }
        return (config);
    }

    public String getValue(String key) {
        return props.getProperty(key);
    }

    public boolean getValueAsBoolean(String key, boolean defValue) {
        String value = getValue(key);
        return StringUtil.isBlank(value) ? defValue : Boolean.valueOf(value);
    }

    public static void main(String[] args){
        Configuration instance = Configuration.getInstance();
        System.out.println(instance.getValue("swiftpass_gateway"));
    }
}
