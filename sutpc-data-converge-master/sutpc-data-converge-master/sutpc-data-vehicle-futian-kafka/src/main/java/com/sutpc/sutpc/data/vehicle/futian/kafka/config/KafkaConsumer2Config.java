package com.sutpc.sutpc.data.vehicle.futian.kafka.config;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.util.StringUtils;

/**
 * .
 * @Description 消费内网kafka集群1消息
 * topic：tpCoachBus,tpTaxi,tpFreight,tpDriving,tpCharterBus,tpTransit,tpDangerous,tpDumper,tpOthers
 * @Author:ShangxiuWu
 * @Date: 16:41 2020/6/22.
 * @Modified By:
 */
@Configuration
public class KafkaConsumer2Config {

  /** kafka自动装配属性类.*/
  @Autowired
  private KafkaProperties kafkaProperties;
  @Value("${spring.kafka.consumer2.bootstrap-servers}")
  private String bootstrapServers;
  @Value("${spring.kafka.consumer2.group-id}")
  private String groupId;

  /**.
   * value为字符串
   * @return
   */
  @Bean
  KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
  kafka2ListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumer2Factory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, String> consumer2Factory() {
    Map<String, Object> configs = kafkaProperties.buildConsumerProperties();
    //解析kafka集群地址
    if (!StringUtils.isEmpty(bootstrapServers)) {
      List<String> servers = Splitter.on(",").splitToList(bootstrapServers);
      List<String> strings = new ArrayList<>();
      strings.addAll(servers);
      configs.put("bootstrap.servers", strings);
    }
    if (!StringUtils.isEmpty(groupId)) {
      configs.put("group.id", groupId);
    }
    return new DefaultKafkaConsumerFactory<>(configs,
        new StringDeserializer(),
        //自定义反序列化器
        new StringDeserializer());
  }
}
