package com.gary.mq.rocket.api;

import com.gary.mq.rocket.server.RocketMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luxinglin
 * @version 1.0
 * @Description: TODO
 * @create 2018-04-28 16:58
 **/
@RestController
@RequestMapping("/v1/messages")
public class MqController {

    @Autowired
    RocketMQProducer rocketMQProducer;

    /**
     * 同步发送
     *
     * @param size
     * @return
     */
    @RequestMapping(value = "send/sync/{size}", method = RequestMethod.GET)
    public String sendSyncMessage(@PathVariable("size") Long size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        rocketMQProducer.sendSync(size);
        stopWatch.stop();
        System.out.println("sendSyncMessage size = " + size + " spend " + stopWatch.getTotalTimeMillis() + " ms");
        return "sync send ok";
    }

    /**
     * 异步发送消息
     *
     * @param size
     * @return
     */
    @RequestMapping(value = "send/async/{size}", method = RequestMethod.GET)
    public String sendAsyncMessage(@PathVariable("size") Long size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        rocketMQProducer.sendAsync(size);
        stopWatch.stop();
        System.out.println("sendAsyncMessage size = " + size + " spend " + stopWatch.getTotalTimeMillis() + " ms");
        return "async send ok";
    }

    /**
     * 单向发送消息
     *
     * @param size
     * @return
     */
    @RequestMapping(value = "send/oneway/{size}", method = RequestMethod.GET)
    public String sendOnewayMessage(@PathVariable("size") Long size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        rocketMQProducer.sendByOneway(size);
        stopWatch.stop();
        System.out.println("sendOnewayMessage size = " + size + " spend " + stopWatch.getTotalTimeMillis() + " ms");
        return "oneway send ok";
    }

    /**
     * 顺序发送消息
     *
     * @param size
     * @return
     */
    @RequestMapping(value = "send/ordered/{size}", method = RequestMethod.GET)
    public String sendOrderedMessage(@PathVariable("size") Long size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        rocketMQProducer.sendByOrder(size);
        stopWatch.stop();
        System.out.println("sendOrderedMessage size = " + size + " spend " + stopWatch.getTotalTimeMillis() + " ms");
        return "ordered send ok";
    }
}
