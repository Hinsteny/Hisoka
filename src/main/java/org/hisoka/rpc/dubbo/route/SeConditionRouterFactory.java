package org.hisoka.rpc.dubbo.route;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.alibaba.dubbo.rpc.cluster.RouterFactory;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class SeConditionRouterFactory implements RouterFactory {

    public static final String EXTENSION_NAME = "condition";

    public static final String NAME = "condition";

    public Router getRouter(URL url) {
        return new SeConditionRouter(url);
    }
}