package com.sutpc.sutpc.data.ic.card.szt.kafka.utils;

import com.sutpc.framework.utils.system.PropertyUtils;
import com.sutpc.sutpc.data.ic.card.szt.kafka.security.LoginUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


@Slf4j
public class KafkaHuaweiConsumerUtils {

  private KafkaConsumer<Integer, String> consumer;

  private Properties props = new Properties();

  /**
   * 构造方法.
   */
  public KafkaHuaweiConsumerUtils() {
    Boolean flag = Boolean.valueOf(PropertyUtils.getProperty("kafka.huawei.client.security.mode"));
    if (flag) {
      try {
        log.info("Securitymode start.");

        //!!注意，安全认证时，需要用户手动修改为自己申请的机机账号
        securityPrepare();
      } catch (IOException e) {
        log.error("Security prepare failure.");
        log.error("The IOException occured : {}.", e);
        return;
      }
      log.info("Security prepare success.");
    }
    init();
    consumer();
    log.info("consumer build success !!!");
  }

  private void securityPrepare() throws IOException {
    LoginUtil.setKrb5Config(
        PropertyUtils.getProperty("kafka.huawei.security.krb5.config.path").replace("\\", "\\\\"));
    LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
    LoginUtil.setJaasFile(PropertyUtils.getProperty("kafka.huawei.user.principal"),
        PropertyUtils.getProperty("kafka.huawei.user.keytab.path").replace("\\", "\\\\"));
  }

  /**
   * NewConsumer构造函数.
   */
  public void init() {
    // Broker连接地址
    props.put("bootstrap.servers", PropertyUtils.getProperty("kafka.huawei.consumer.server"));
    // Group id
    props.put("group.id", PropertyUtils.getProperty("kafka.huawei.consumer.group-id"));
    // 是否自动提交offset
    props.put("enable.auto.commit",
        PropertyUtils.getProperty("kafka.huawei.consumer.enable.auto.commit"));
    // 自动提交offset的时间间隔
    props.put("auto.commit.interval.ms",
        PropertyUtils.getProperty("kafka.huawei.consumer.auto.commit.interval.ms"));
    // 会话超时时间
    props.put("session.timeout.ms",
        PropertyUtils.getProperty("kafka.huawei.consumer.session.timeout.ms"));
    // 消息Key值使用的反序列化类
    props.put("key.deserializer",
        PropertyUtils.getProperty("kafka.huawei.consumer.key.deserializer"));
    // 消息内容使用的反序列化类
    props.put("value.deserializer",
        PropertyUtils.getProperty("kafka.huawei.consumer.value.deserializer"));
    // 安全协议类型
    props.put("security.protocol",
        PropertyUtils.getProperty("kafka.huawei.consumer.security.protocol"));
    // 服务名
    props.put("sasl.kerberos.service.name",
        PropertyUtils.getProperty("kafka.huawei.consumer.sasl.kerberos.service.name"));
    // 域名
    props.put("kerberos.domain.name",
        PropertyUtils.getProperty("kafka.huawei.consumer.kerberos.domain.name"));
    // 从哪儿开始消费
    props.put("auto.offset.reset",
        PropertyUtils.getProperty("kafka.huawei.consumer.auto.offset.reset"));
  }

  private void consumer() {
    consumer = new KafkaConsumer(props);
  }

  public void receive(java.util.function.Consumer<ConsumerRecord<Integer, String>> receive) {
    receive(null, receive);
  }

  /**
   * 接收到消费的消息.
   *
   * @param topics kafka的topic名称
   * @param receive Lambda表达式
   */
  public void receive(List<String> topics,
      java.util.function.Consumer<ConsumerRecord<Integer, String>> receive) {
    // 订阅
    consumer.subscribe(
        topics == null ? Arrays.asList(PropertyUtils.getProperty("kafka.huawei.consumer.topic"))
            : topics);
    while (true) {
      // 消息消费请求
      ConsumerRecords<Integer, String> records = consumer.poll(1000);
      // 消息处理
      records.forEach(t -> receive.accept(t));
    }
  }

  /**
   * 接收到消费的消息.
   *
   * @param topic kafka的topic名称
   * @param receive Lambda表达式
   */
  public void receiveSleep(List<String> topic,
      java.util.function.Consumer<ConsumerRecords<Integer, String>> receive) {
    consumer.subscribe(
        topic == null ? Arrays.asList(PropertyUtils.getProperty("kafka.huawei.consumer.topic"))
            : topic);

    while (true) {
      // 消息消费请求
      ConsumerRecords<Integer, String> records = consumer.poll(1000);
      // 消息处理
      receive.accept(records);
    }
  }
}
