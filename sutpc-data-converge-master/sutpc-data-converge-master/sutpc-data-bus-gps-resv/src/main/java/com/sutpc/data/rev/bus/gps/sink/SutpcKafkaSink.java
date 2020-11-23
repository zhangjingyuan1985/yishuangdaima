package com.sutpc.data.rev.bus.gps.sink;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SutpcKafkaSink {

  @Value("${sutpc.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${sutpc.kafka.topic}")
  private String topic;

  /**
   *  .
   * @return
   */
  @Bean(name = "suptcKafkaTemplate")
  public KafkaTemplate<String, String> kafkaTemplate() {
    Map props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.RETRIES_CONFIG, 3);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(
        new DefaultKafkaProducerFactory<>(props), true);
    kafkaTemplate.setDefaultTopic(topic);
    return kafkaTemplate;
  }



}
