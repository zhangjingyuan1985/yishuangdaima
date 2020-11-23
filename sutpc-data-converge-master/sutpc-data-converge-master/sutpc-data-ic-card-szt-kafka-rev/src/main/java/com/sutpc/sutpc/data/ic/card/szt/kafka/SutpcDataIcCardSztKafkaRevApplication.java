package com.sutpc.sutpc.data.ic.card.szt.kafka;

import com.sutpc.sutpc.data.ic.card.szt.kafka.properties.HuaWeiKafkaProperties;
import com.sutpc.sutpc.data.ic.card.szt.kafka.utils.KafkaHuaweiConsumerUtils;
import com.sutpc.sutpc.data.ic.card.szt.kafka.utils.KafkaHuaweiProducerUtils;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
@Slf4j
public class SutpcDataIcCardSztKafkaRevApplication implements CommandLineRunner {

  @Value("${kafka.huawei.consumer.topic}")
  private String topic;
  @Autowired
  private HuaWeiKafkaProperties huaWeiKafkaProperties;

  public static void main(String[] args) {
    SpringApplication.run(SutpcDataIcCardSztKafkaRevApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    afterPropertiesSet();
    KafkaHuaweiConsumerUtils kafkaHuaweiConsumerUtils = new KafkaHuaweiConsumerUtils();
    KafkaHuaweiProducerUtils kafkaHuaweiProducerUtils = new KafkaHuaweiProducerUtils();
    kafkaHuaweiConsumerUtils.receive(integerStringConsumerRecord -> {
      log.info(integerStringConsumerRecord.value());
      kafkaHuaweiProducerUtils
          .send(integerStringConsumerRecord.value());
    });
  }

  /**
   * @Description 初始化key文件.
   * @Author:wusx
   * @Date 15:57 2020/6/22
   * @Modified
   */
  private void afterPropertiesSet() throws Exception {
    initKey(huaWeiKafkaProperties.getKeytabPath(), "key/user.keytab");
    initKey(huaWeiKafkaProperties.getConfigPath(), "key/krb5.conf");
  }

  private void initKey(String path, String target) throws Exception {
    File file = new File(path);
    if (!file.exists()) {
      ClassPathResource classPathResource = new ClassPathResource(target);
      classPathResource.getInputStream();
      Files.copy(classPathResource.getInputStream(), file.toPath());
    }
  }
}
