package com.sutpc.sutpc.data.vehicle.futian.kafka.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

/**
 * .
 * @Description 消费内网kafka集群消息，topic：topic_gps_bus
 * @Author:ShangxiuWu
 * @Date: 16:41 2020/6/22.
 * @Modified By:
 */
@Configuration
public class KafkaConsumerConfig {

  /** kafka自动装配属性类.*/
  @Autowired
  private KafkaProperties kafkaProperties;

  /**.
   * value为字符串
   * @return
   */
  @Bean
  KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
  kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(),
        new StringDeserializer(),
        //自定义反序列化器
        new StringDeserializer());
  }
}
