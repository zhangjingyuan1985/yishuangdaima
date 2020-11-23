package com.sutpc.sutpc.data.vehicle.futian.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 17:01 2020/6/22.
 * @Modified By:
 */
@Component
@Slf4j
public class GpsBusListener {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @KafkaListener(id = "${spring.kafka.consumer.group-id}",
      topics = "#{'${spring.kafka.template.topic}'.split(',')}",
      containerFactory = "kafkaListenerContainerFactory")
  public void listen(ConsumerRecord<Integer, String> record) {
    log.debug("topic:{}-{}", record.topic(), record.value());
    kafkaTemplate.send(record.topic(), record.value());
  }

}
