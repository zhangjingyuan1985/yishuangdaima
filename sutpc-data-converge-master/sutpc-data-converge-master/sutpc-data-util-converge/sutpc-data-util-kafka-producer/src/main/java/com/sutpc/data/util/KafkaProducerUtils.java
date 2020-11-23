package com.sutpc.data.util;

import com.sutpc.framework.utils.system.PropertyUtils;
import java.util.Properties;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Kafka生产者工具类.
 * @author minyikun
 */
@Slf4j
public class KafkaProducerUtils {

  /**
   * 属性文件.
   */
  private Properties props;
  /**
   * 生产者对象.
   */
  private Producer<String, String> producer;


  /**
   * 无参构造函数.
   */
  public KafkaProducerUtils() {
    props = new Properties();
    init(null);
    producer();

  }

  /**
   *  构造函数.
   * @param server server的地址
   */
  public KafkaProducerUtils(String server) {
    props = new Properties();
    init(server);
    producer();
  }

  /**
   * 初始化.
   */
  private void init(String server) {
    props.put("bootstrap.servers",
        server == null ? PropertyUtils.getProperty("kafka.producer.server")
            : server);//服务器ip:端口号，集群用逗号分隔
    props.put("acks", PropertyUtils.getProperty("kafka.producer.acks") == null ? "all"
        : PropertyUtils.getProperty("kafka.producer.acks"));
    props.put("retries", PropertyUtils.getProperty("kafka.producer.retries") == null ? 0
        : PropertyUtils.getProperty("kafka.producer.retries"));
    props.put("batch.size", PropertyUtils.getProperty("kafka.producer.batch.size") == null ? 16384
        : PropertyUtils.getProperty("kafka.producer.batch.size"));
    props.put("linger.ms", PropertyUtils.getProperty("kafka.producer.linger.ms") == null ? 1
        : PropertyUtils.getProperty("kafka.producer.linger.ms"));
    props.put("buffer.memory",
        PropertyUtils.getProperty("kafka.producer.buffer.memory") == null ? 33554432
            : PropertyUtils.getProperty("kafka.producer.buffer.memory"));
    props.put("key.serializer", PropertyUtils.getProperty("kafka.producer.key.serializer") == null
        ? "org.apache.kafka.common.serialization.StringSerializer"
        : PropertyUtils.getProperty("kafka.producer.key.serializer"));
    props.put("value.serializer",
        PropertyUtils.getProperty("kafka.producer.value.serializer") == null
            ? "org.apache.kafka.common.serialization.StringSerializer"
            : PropertyUtils.getProperty("kafka.producer.value.serializer"));
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
    String topic = PropertyUtils.getProperty("kafka.producer.topic");
    Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(topic, msg));
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
    Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(topic, msg));
    return send;
  }

  /**
   *  .
   * @param args 参数
   */
  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "127.0.0.1:9092");//服务器ip:端口号，集群用逗号分隔
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    Producer<String, String> producer = new KafkaProducer<>(props);
    Future<RecordMetadata> send = producer
        .send(new ProducerRecord<String, String>("tp_ncp_jsywz", "aaaa"));
    System.out.println(send);
  }
}
