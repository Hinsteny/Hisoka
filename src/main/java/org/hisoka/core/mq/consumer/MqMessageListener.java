package org.hisoka.core.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hisoka.common.constance.Application;
import org.hisoka.common.util.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.util.Map;

/**
 * @author Hinsteny
 * @date 2016/8/16
 * @copyright: 2016 All rights reserved.
 */
public abstract class MqMessageListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MqMessageListener.class);

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static boolean openLog;

    private static int logLength;

    public static void setOpenLog(boolean openLog) {
        MqMessageListener.openLog = openLog;
    }

    public static void setLogLength(int logLength) {
        MqMessageListener.logLength = logLength;
    }

    public abstract Object handleMessage(String messageId, String messageContent, String queue);

    @Override
    public void onMessage(Message message) {
        if (openLog) {
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            Object obj = null;
            String messageContent = "";
            String queue = "";
            String logId = "";
            String messageId = "";

            try {
                messageId = message.getMessageProperties().getMessageId();
                messageContent = new String(message.getBody(), DEFAULT_CHARSET);
                MessageProperties messageProperties = message.getMessageProperties();
                queue = messageProperties.getReceivedRoutingKey();
                Map<String, Object> headers = messageProperties.getHeaders();

                if (headers != null) {

                }

                obj = handleMessage(messageId, messageContent, queue);
            } catch (Throwable t) {
                obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
                log.error("Handle mq message error, message=" + message, t);
            } finally {
                String mqResult = "";

                if (obj != null) {
                    mqResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
                }

                endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
                // 鎵撳嵃鏃ュ織
                String messageLog = getMessageLog(messageId, messageContent, queue, mqResult, startTime, endTime);
                int logLength = MqMessageListener.logLength != 0 ? MqMessageListener.logLength : Application.LOG_MAX_LENGTH;

                if (logLength != -1 && messageLog.length() > logLength) {
                    messageLog = messageLog.substring(0, logLength);
                }

            }
        } else {
            try {
                String messageId = message.getMessageProperties().getMessageId();
                String messageContent = new String(message.getBody(), DEFAULT_CHARSET);
                String queue = message.getMessageProperties().getReceivedRoutingKey();
                handleMessage(messageId, messageContent, queue);
            } catch (Throwable t) {
                log.error("Handle mq message error, message=" + message, t);
            }
        }
    }

    private String getMessageLog(String messageId, String messageContent, String queue, String mqResult, long startTime, long endTime) {
        long cost = endTime - startTime;
        String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
        String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
        return String.format("[RabbitMqConsumer] Receive mq message, messageId:%s|messageContent:%s|queue:%s|result:%s|[start:%s, end:%s, cost:%dms]", messageId,
                messageContent, queue, mqResult, startTimeStr, endTimeStr, cost);
    }


}
