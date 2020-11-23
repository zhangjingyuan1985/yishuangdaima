package com.sutpc.data.util;

import com.sutpc.data.util.security.LoginUtil;
import com.sutpc.framework.utils.system.PropertyUtils;
import java.io.IOException;
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
    Boolean flag = Boolean.valueOf(PropertyUtils.getProperty("kafka.huawei.client.security.mode"));
    if (flag) {
      try {
        log.info("Securitymode start.");

        // !!注意，安全认证时，需要用户手动修改为自己申请的机机账号
        securityPrepare();
      } catch (IOException e) {
        log.error("Security prepare failure.");
        log.error("The IOException occured.", e);
        return;
      }
      log.info("Security prepare success.");
    }

    init();
    producer();
    log.info("producer build success !!!");
  }

  /**
   * 安全认证.
   *
   * @throws IOException 如果文件不存在,抛出
   */
  public void securityPrepare() throws IOException {
    LoginUtil.setKrb5Config(
        PropertyUtils.getProperty("kafka.huawei.security.krb5.config.path").replace("\\", "\\\\"));
    LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
    LoginUtil.setJaasFile("test",
        PropertyUtils.getProperty("kafka.huawei.user.keytab.path").replace("\\", "\\\\"));
  }

  /**
   * 初始化.
   */
  public void init() {
    // Broker地址列表
    props.put("bootstrap.servers", "10.143.200.20:21007,10.143.200.19:21007");
    // 客户端ID
    props.put("client.id", "huanshuiProducer1");
    // Key序列化类
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    // Value序列化类
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    // 协议类型:当前支持配置为SASL_PLAINTEXT或者PLAINTEXT
    props.put("security.protocol", "SASL_PLAINTEXT");
    // 服务名
    props.put("sasl.kerberos.service.name", "kafka");
    // 域名
    props.put("kerberos.domain.name", "hadoop.hadoop.com");
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
    String topic = PropertyUtils.getProperty("kafka.huawei.producer.topic");
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