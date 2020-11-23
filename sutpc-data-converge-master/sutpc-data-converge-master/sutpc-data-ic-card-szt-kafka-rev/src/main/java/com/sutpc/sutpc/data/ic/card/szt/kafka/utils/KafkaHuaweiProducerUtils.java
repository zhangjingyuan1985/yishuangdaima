package com.sutpc.sutpc.data.ic.card.szt.kafka.utils;

import com.sutpc.framework.utils.system.PropertyUtils;
import java.util.Properties;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class KafkaHuaweiProducerUtils {

  private KafkaProducer<Integer, String> producer;

  private final Properties props = new Properties();

  /**
   * 构造方法.
   */
  public KafkaHuaweiProducerUtils() {
    init();
    producer();
    log.info("producer build success !!!");
  }


  /**
   * 初始化.
   */
  public void init() {
    // Broker地址列表
    props.put("bootstrap.servers", PropertyUtils.getProperty("spring.kafka.producer.bootstrap-servers"));
    // 客户端ID
    props.put("client.id", "huanshuiProducer1");
    // Key序列化类
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    // Value序列化类
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  }

  /**
   * 创建生产者.
   */
  private void producer() {
    producer = new KafkaProducer<>(props);
  }

  /**
   * 发送消息.
   *
   * @param msg 消息
   * @return 返回一个结果元数据
   */
  public Future<RecordMetadata> send(String msg) {
    String topic = PropertyUtils.getProperty("spring.kafka.producer.topic");
    Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topic, msg));
    return send;
  }

  /**
   * 发送消息.
   *
   * @param topic 主题
   * @param msg 消息
   * @return 返回一个结果元数据
   */
  public Future<RecordMetadata> send(String topic, String msg) {
    log.info("topic:{},message:{}", topic, msg);
    Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topic, msg));
    return send;
  }
}