package com.sutpc.data.util;

import com.sutpc.framework.utils.system.PropertyUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@Slf4j
public class KafkaConsumerUtils {

  /**
   * 属性文件.
   */
  private Properties props;
  /**
   * 消费者对象.
   */
  private Consumer<String, String> consumer;

  /**
   * 无参构造函数 .
   */
  public KafkaConsumerUtils() {
    props = new Properties();
    init(null);
    consumer();
  }

  /**
   * 构造函数.
   *
   * @param server 服务端地址
   */
  public KafkaConsumerUtils(String server) {
    props = new Properties();
    init(server);
    consumer();
  }

  /**
   * 初始化.
   */
  private void init(String server) {
    props = new Properties();
    props.put("bootstrap.servers",
        server == null ? PropertyUtils.getProperty("kafka.consumer.server")
            : server);//服务器ip:端口号，集群用逗号分隔
    props.put("group.id", PropertyUtils.getProperty("kafka.consumer.group-id"));
    props.put("enable.auto.commit",
        PropertyUtils.getProperty("kafka.consumer.enable.auto.commit") == null ? "true"
            : PropertyUtils.getProperty("kafka.consumer.enable.auto.commit"));
    props.put("auto.commit.interval.ms",
        PropertyUtils.getProperty("kafka.consumer.auto.commit.interval.ms") == null ? "1000"
            : PropertyUtils.getProperty("kafka.consumer.auto.commit.interval.ms"));
    props.put("session.timeout.ms",
        PropertyUtils.getProperty("kafka.consumer.session.timeout.ms") == null ? "30000"
            : PropertyUtils.getProperty("kafka.consumer.session.timeout.ms"));
    props.put("key.deserializer",
        PropertyUtils.getProperty("kafka.consumer.key.deserializer") == null
            ? "org.apache.kafka.common.serialization.StringDeserializer"
            : PropertyUtils.getProperty("kafka.consumer.key.deserializer"));
    props.put("value.deserializer",
        PropertyUtils.getProperty("kafka.consumer.value.deserializer") == null
            ? "org.apache.kafka.common.serialization.StringDeserializer"
            : PropertyUtils.getProperty("kafka.consumer.value.deserializer"));
    props.put("auto.offset.reset",
        PropertyUtils.getProperty("kafka.consumer.auto.offset.reset") == null ? "earliest"
            : PropertyUtils.getProperty("kafka.consumer.auto.offset.reset"));
    props.put("max.poll.records",
        PropertyUtils.getProperty("kafka.consumer.max.poll.records") == null ? 100
            : PropertyUtils.getProperty("kafka.consumer.max.poll.records"));
    props.put("max.poll.interval.ms",
        PropertyUtils.getProperty("kafka.consumer.max.poll.interval.ms") == null ? 10 * 60 * 1000
            : PropertyUtils.getProperty("kafka.consumer.max.poll.interval.ms"));
  }

  private void consumer() {
    consumer = new KafkaConsumer(props);
  }

  public void receive(java.util.function.Consumer<ConsumerRecord<String, String>> receive) {
    receive(null, receive);
  }

  /**
   * 从kafka队列接收数据.
   *
   * @param topics 主题
   * @param receive 回调函数
   */
  public void receive(List<String> topics,
      java.util.function.Consumer<ConsumerRecord<String, String>> receive) {
    consumer.subscribe(
        topics == null ? Arrays.asList(PropertyUtils.getProperty("kafka.consumer.topic")) : topics);
    try {
      while (true) {

        ConsumerRecords<String, String> records = consumer.poll(100);
        if (records.count() == 0) {
          log.info("records size:{}", records.count());
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            log.error("error message:{}", e.getMessage());
          }
        }
        if (records.count() > 0) {
          records.forEach(t -> receive.accept(t));
        }
      }
    } finally {
      consumer.close();
    }
  }

  /**
   * 从kafka队列接收数据.
   *
   * @param topic 主题
   * @param receive 回调函数
   */
  public void receiveSleep(List<String> topic,
      java.util.function.Consumer<ConsumerRecords<String, String>> receive) {
    consumer.subscribe(
        topic == null ? Arrays.asList(PropertyUtils.getProperty("kafka.consumer.topic")) : topic);
    try {
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);

        if (records.count() == 0) {
          log.info("records size:{}", records.count());
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            log.error("error message:{}", e.getMessage());
          }
        }
        if (records.count() > 0) {
          receive.accept(records);
        }
      }
    } finally {
      consumer.close();
    }
  }

  /**
   * zookeeper.conn连接方式消费kafka
   */
  public static void zookeeperConnReceive(java.util.function.Consumer<String> receive) {
    Properties properties = new Properties();
    properties.put("zookeeper.connect", PropertyUtils.getProperty("zookeeper.connect"));
    properties.put("group.id", PropertyUtils.getProperty("zookeeper.group.id"));
    log.info("prop :{}", properties.toString());
    ConsumerConnector consumer = kafka.consumer.Consumer
        .createJavaConsumerConnector(new ConsumerConfig(properties));
    String topic = PropertyUtils.getProperty("zookeeper.consumer.topic");
    log.info("topic:{}", topic);
    Map<String, Integer> topicCountMap = new HashMap<>();
    topicCountMap.put(topic, 1);
    Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer
        .createMessageStreams(topicCountMap);
    KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);
    ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
    while (iterator.hasNext()) {
      String message = new String(iterator.next().message());
      receive.accept(message);
    }
  }

  /**
   * 身份认证.
   *
   * @param receive 回调函数
   */
  public static void auth(java.util.function.Consumer<ConsumerRecord<String, String>> receive) {
    Properties props = new Properties();
    System.setProperty("java.security.auth.login.config",
        PropertyUtils.getProperty("kafka.consumer.cert.url"));
    props.put("security.protocol", PropertyUtils.getProperty("kafka.security.protocol"));
    props.put("sasl.mechanism", PropertyUtils.getProperty("kafka.sasl.mechanism"));

    props.put("bootstrap.servers", PropertyUtils.getProperty("kafka.consumer.server"));

    props.put("group.id", PropertyUtils.getProperty("kafka.consumer.group-id"));
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    String topic = PropertyUtils.getProperty("kafka.consumer.topic");
    consumer.subscribe(Arrays.asList(topic));
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(100);
      if (records.count() == 0) {
        log.info("records size:{}", records.count());
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          log.error("error message:{}", e.getMessage());
        }
      }
      if (records.count() > 0) {
        records.forEach(t -> receive.accept(t));
      }
    }
  }
}