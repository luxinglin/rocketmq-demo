package com.gary.mq.rocket.server;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhisheng_tian on 2018/2/6
 */
@Component
public class RocketMQProducer {
    DefaultMQProducer producer;
    /**
     * 生产者的组名
     */
    @Value("${apache.rocketmq.producer.producerGroup}")
    private String producerGroup;
    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    /**
     * 单向发送，一般用于日志收集场景
     *
     * @param size
     */
    public void sendByOneway(long size) {
        try {
            start();
            for (int i = 0; i < 100; i++) {
                //Create a message instance, specifying topic, tag and message body.
                Message msg = new Message("TopicTest" /* Topic */,
                        "TagA" /* Tag */,
                        ("Hello RocketMQ " +
                                i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
                );
                //Call send message to deliver message to one of brokers.
                producer.sendOneway(msg);

            }

        } catch (Exception ex) {

        } finally {
            //Shut down once the producer instance is not longer in use.
            producer.shutdown();
        }
    }

    /**
     * 同步发送
     *
     * @param size
     */
    public void sendSync(long size) {
        //Launch the instance.
        try {
            start();
            for (int i = 0; i < size; i++) {
                //Create a message instance, specifying topic, tag and message body.
                /* Topic */
                Message msg = new Message("TopicTest",
                        /* Tag */
                        "TagA",
                        ("Hello RocketMQ " +
                                /* Message body */
                                i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );
                //Call send message to deliver message to one of brokers.
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Shut down once the producer instance is not longer in use.
            producer.shutdown();
        }
    }

    /**
     * 异步发送
     *
     * @param size
     */
    public void sendAsync(long size) {
        try {
            //Launch the instance.
            start();
            producer.setRetryTimesWhenSendAsyncFailed(0);
            for (int i = 0; i < size; i++) {
                final int index = i;
                //Create a message instance, specifying topic, tag and message body.
                Message msg = new Message("TopicTest",
                        "TagA",
                        "OrderID188",
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%-10d OK %s %n", index,
                                sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception ex) {

        } finally {
            //Shut down once the producer instance is not longer in use.
            producer.shutdown();
        }
    }

    /**
     * 顺序发送
     *
     * @param size
     */
    public void sendByOrder(long size) {
        try {
            //Instantiate with a producer group name.
            start();
            String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
            for (int i = 0; i < size; i++) {
                int orderId = i % 10;
                //Create a message instance, specifying topic, tag and message body.
                Message msg = new Message("TopicTestOrder", tags[i % tags.length], "KEY" + i,
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, orderId);

                System.out.printf("%s%n", sendResult);
            }
        } catch (Exception ex) {
        } finally {
            //server shutdown
            producer.shutdown();
        }
    }

    /**
     * 启动发送者实例
     *
     * @throws MQClientException
     */
    private void start() throws MQClientException {
        //生产者的组名
        producer = new DefaultMQProducer(producerGroup);
        ///producer.setInstanceName(RunTimeUtil.getRocketMqUniqeInstanceName());
        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesrvAddr);
        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        producer.start();
    }


    private Message getMessage(int i, String topic) throws UnsupportedEncodingException {
        Map<String, Object> param = new HashMap<>();
        for (int idx = 0; idx < 10000; idx++) {
            param.put(i + "_" + idx, "value=" + idx);
        }
        Message message = new Message(topic, "push", param.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));

        return message;
    }
}