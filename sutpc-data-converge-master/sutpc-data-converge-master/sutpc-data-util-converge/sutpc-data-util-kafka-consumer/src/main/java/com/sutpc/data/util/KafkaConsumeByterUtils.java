package com.sutpc.data.util;

import com.sutpc.framework.utils.system.PropertyUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/20 16:39
 */
@Slf4j
public class KafkaConsumeByterUtils {

  private Consumer<String, byte[]> consumer;
  /**
   * 属性文件.
   */
  private Properties props = new Properties();

  /**
   *  .
   */
  public KafkaConsumeByterUtils() {
    props = new Properties();
    init(null);
    consumer();
  }

  public KafkaConsumeByterUtils(String server) {
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

  public void receive(java.util.function.Consumer<ConsumerRecord<String, byte[]>> receive) {
    receive(null, receive);
  }

  /**
   * 从topic接收数据.
   * @param topics 主题
   * @param receive 回调函数
   */
  public void receive(List<String> topics,
      java.util.function.Consumer<ConsumerRecord<String, byte[]>> receive) {
    consumer.subscribe(
        topics == null ? Arrays.asList(PropertyUtils.getProperty("kafka.consumer.topic")) : topics);
    try {
      while (true) {

        ConsumerRecords<String, byte[]> records = consumer.poll(100);

        log.info("records size:{}", records.count());
        if (records.count() > 0) {
          records.forEach(t -> receive.accept(t));
        }
      }
    } finally {
      consumer.close();
    }
  }

  /**
   * 从kafka接收数据.
   * @param topic 主题
   * @param receive 回调函数
   */
  public void receiveSleep(List<String> topic,
      java.util.function.Consumer<ConsumerRecords<String, byte[]>> receive) {
    consumer.subscribe(
        topic == null ? Arrays.asList(PropertyUtils.getProperty("kafka.consumer.topic")) : topic);
    try {
      while (true) {
        ConsumerRecords<String, byte[]> records = consumer.poll(100);

        log.info("records size:{}", records.count());
        if (records.count() > 0) {
          receive.accept(records);
        }
      }
    } finally {
      consumer.close();
    }
  }
}
