package com.sutpc.demo.listener;

import com.sutpc.demo.model.CacheModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * kafka 消费者
 */
@Component
public class BusinessListener {

    private Logger logger = LoggerFactory.getLogger(BusinessListener.class);

    @KafkaListener(topics = "${spring.kafka.consumer.bus.topic}")
    public void listenCar(String msg) {
        CacheModel.setCarTime(System.currentTimeMillis());
        logger.debug("监听kafka 数据-> 实时重点车辆 : " + msg);
    }

    @KafkaListener(topics = "${spring.kafka.consumer.car.topic}")
    public void listenBus(String msg) {
        CacheModel.setBusTime(System.currentTimeMillis());
        logger.debug("监听kafka 数据-> 实时公交 : " + msg);
    }

}
