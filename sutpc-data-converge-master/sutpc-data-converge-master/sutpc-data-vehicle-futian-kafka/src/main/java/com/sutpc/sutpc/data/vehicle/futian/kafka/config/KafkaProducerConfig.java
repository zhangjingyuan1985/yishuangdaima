package com.sutpc.sutpc.data.vehicle.futian.kafka.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * .
 * @Description 生产者配置，发送数据到福田中心区kafka集群
 * @Author:ShangxiuWu
 * @Date: 16:35 2020/6/22.
 * @Modified By:
 */
@Configuration
public class KafkaProducerConfig {

  /** kafka自动装配属性类.*/
  @Autowired
  private KafkaProperties kafkaProperties;

  /**.
   * @Author wusx
   * @Date 17:26 2019/11/22 0022
   * @Description 生产者工厂.
   * @Modified
   */
  @Bean
  public ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(),
        new StringSerializer(),
        //自定义序列化器
        new StringSerializer());
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate(producerFactory());
  }
}
