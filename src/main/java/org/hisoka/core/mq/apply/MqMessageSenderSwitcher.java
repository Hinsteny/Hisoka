package org.hisoka.core.mq.apply;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class MqMessageSenderSwitcher {

    private static final ThreadLocal<String> mqMessageSenderContextHolder = new ThreadLocal<String>();

    public static void setMqMessageSenderTypeInContext(String mqMessageSenderContext) {
        mqMessageSenderContextHolder.set(mqMessageSenderContext);
    }

    public static String getMqMessageSenderType() {
        String mqMessageSenderType = (String) mqMessageSenderContextHolder.get();
        return mqMessageSenderType;
    }

    public static String getMqMessageSenderTypeFromContext() {
        String mqMessageSenderType = (String) mqMessageSenderContextHolder.get();
        return mqMessageSenderType;
    }

    public static void clearMqMessageSenderContextType() {
        mqMessageSenderContextHolder.remove();
    }

}
