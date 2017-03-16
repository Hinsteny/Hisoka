package org.hisoka.core.mq.apply;

import org.hisoka.common.constance.Application;
import org.hisoka.common.util.other.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hisoka.common.exception.SystemException;
import org.hisoka.common.util.reflect.ReflectUtil;
import org.hisoka.common.util.date.DateUtil;
import org.hisoka.common.util.other.UUIDUtil;
import org.hisoka.core.mq.producer.MqMessageSender;

import java.util.Map;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/10/24
 * @copyright: 2016 All rights reserved.
 */
public class DynamicMqMessageSender implements MqMessageSender {

    private static final Logger log = LoggerFactory.getLogger(DynamicMqMessageSender.class);

    private static final String SUCCESS = "success";

    private static final String DEFAULT_MESSAGE_ENCODE = "UTF-8";

    private static final MessageDeliveryMode DEFAULT_MESSAGE_DELIVERY = MessageDeliveryMode.PERSISTENT;

    private RabbitTemplate defaultTargetRabbitTemplate;

    private Map<String, RabbitTemplate> targetRabbitTemplates;

    /**
     * 日志开关，默认为false不打开
     */
    private boolean openLog = false;

    /**
     * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
     */
    private int logLength;

    public void setDefaultTargetRabbitTemplate(RabbitTemplate defaultTargetRabbitTemplate) {
        this.defaultTargetRabbitTemplate = defaultTargetRabbitTemplate;
    }

    public void setTargetRabbitTemplates(Map<String, RabbitTemplate> targetRabbitTemplates) {
        this.targetRabbitTemplates = targetRabbitTemplates;
    }

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void setLogLength(int logLength) {
        this.logLength = logLength;
    }

    public String sendMessage(final Object messageObj) {
        RabbitTemplate rabbitTemplate = getRabbitTemplate();

        if (rabbitTemplate == null) {
            rabbitTemplate = defaultTargetRabbitTemplate;
        }

        if (rabbitTemplate == null) {
            throw new SystemException("Can not get a rabbitTemplate!");
        }

        boolean flag = false;
        final String messageId = UUIDUtil.getJustNumUUID();

        if (openLog) {
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            String messageContent = "";
            String exchange = (String) ReflectUtil.getFieldValue(rabbitTemplate, "exchange");
            String routingKey = (String) ReflectUtil.getFieldValue(rabbitTemplate, "routingKey");
            Object obj = null;

            try {
                messageContent = JSON.toJSONStringWithDateFormat(messageObj, DateUtil.MAX_LONG_DATE_FORMAT_STR,
                        SerializerFeature.DisableCircularReferenceDetect);

//				rabbitTemplate.setConfirmCallback(new ConfirmCallback(){
//					@Override
//					public void confirm(CorrelationData correlationData, boolean ack) {
//						String correlationDataId = correlationData.getId();
//
//						if (ack) {
//							System.out.println("y");
//						} else {
//							System.out.println("n");
//						}
//					}
//				});

                rabbitTemplate.convertAndSend(messageObj, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setMessageId(messageId);
                        messageProperties.setContentEncoding(DEFAULT_MESSAGE_ENCODE);
                        messageProperties.setDeliveryMode(DEFAULT_MESSAGE_DELIVERY);
                        messageProperties.setHeader(LogUtil.LOG_ID, LogUtil.getLogId());
                        return message;
                    }
                });

                obj = SUCCESS;
                flag = true;
            } catch (Throwable t) {
                obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
                log.error("Send mq message error, message=" + messageObj, t);
            } finally {
                String mqResult = "";
                String messageIdStr = "";

                if (obj != null) {
                    mqResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
                }

                if (flag) {
                    messageIdStr = messageId;
                }

                endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
                // 打印日志
                String messageLog = getMessageLog(messageIdStr, messageContent, exchange, routingKey, mqResult, startTime, endTime);
                int logLength = this.logLength != 0 ? this.logLength : Application.LOG_MAX_LENGTH;

                if (logLength != -1 && messageLog.length() > logLength) {
                    messageLog = messageLog.substring(0, logLength);
                }

                LogUtil.log(log, messageLog);
            }
        } else {
            rabbitTemplate.convertAndSend(messageObj, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setMessageId(messageId);
                    return message;
                }
            });

            flag = true;
        }

        if (flag) {
            return messageId;
        } else {
            return null;
        }
    }

    public String sendMessage(String exchange, String routingKey, final Object messageObj) {
        RabbitTemplate rabbitTemplate = getRabbitTemplate();

        if (rabbitTemplate == null) {
            rabbitTemplate = defaultTargetRabbitTemplate;
        }

        if (rabbitTemplate == null) {
            throw new SystemException("Can not get a rabbitTemplate!");
        }

        boolean flag = false;
        final String messageId = UUIDUtil.getJustNumUUID();

        if (openLog) {
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            String messageContent = "";
            Object obj = null;

            try {
                messageContent = JSON.toJSONStringWithDateFormat(messageObj, DateUtil.MAX_LONG_DATE_FORMAT_STR,
                        SerializerFeature.DisableCircularReferenceDetect);

//				rabbitTemplate.setConfirmCallback(new ConfirmCallback(){
//					@Override
//					public void confirm(CorrelationData correlationData, boolean ack) {
//						String correlationDataId = correlationData.getId();
//
//						if (ack) {
//							System.out.println("y");
//						} else {
//							System.out.println("n");
//						}
//					}
//				});
//
                rabbitTemplate.convertAndSend(exchange, routingKey, messageObj, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setMessageId(messageId);
                        messageProperties.setContentEncoding(DEFAULT_MESSAGE_ENCODE);
                        messageProperties.setDeliveryMode(DEFAULT_MESSAGE_DELIVERY);
                        messageProperties.setHeader(LogUtil.LOG_ID, LogUtil.getLogId());
                        return message;
                    }
                });

                obj = SUCCESS;
                flag = true;
            } catch (Throwable t) {
                obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
                log.error("Send mq message error, message=" + messageObj, t);
                throw t;
            } finally {
                String mqResult = "";
                String messageIdStr = "";

                if (obj != null) {
                    mqResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
                }

                if (flag) {
                    messageIdStr = messageId;
                }

                endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
                // 打印日志
                String messageLog = getMessageLog(messageIdStr, messageContent, exchange, routingKey, mqResult, startTime, endTime);
                LogUtil.log(log, messageLog);
            }
        } else {
            rabbitTemplate.convertAndSend(exchange, routingKey, messageObj, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setMessageId(messageId);
                    return message;
                }
            });

            flag = true;
        }

        if (flag) {
            return messageId;
        } else {
            return null;
        }
    }

    private String getMessageLog(String messageId, String messageContent, String exchange, String routingKey, String mqResult, long startTime, long endTime) {
        long cost = endTime - startTime;
        String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
        String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
        return String.format("[RabbitMqProducer] Send mq message, messageId:%s|messageContent:%s|exchange:%s|routingKey:%s|result:%s|[start:%s, end:%s, cost:%dms]",
                messageId, messageContent, exchange, routingKey, mqResult, startTimeStr, endTimeStr, cost);
    }

    private RabbitTemplate getRabbitTemplate() {
        String mqMessageSender = MqMessageSenderSwitcher.getMqMessageSenderType();

        if (mqMessageSender != null && !"".equals(mqMessageSender)) {
            RabbitTemplate rabbitTemplate = targetRabbitTemplates.get(mqMessageSender);
            return rabbitTemplate;
        } else {
            return null;
        }
    }

    public void afterPropertiesSet() {

    }

    public void initMqLog() {

    }
}
