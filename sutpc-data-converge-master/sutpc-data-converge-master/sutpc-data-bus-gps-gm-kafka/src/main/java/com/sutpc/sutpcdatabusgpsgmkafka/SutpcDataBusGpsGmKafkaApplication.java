package com.sutpc.sutpcdatabusgpsgmkafka;

import com.sutpc.sutpcdatabusgpsgmkafka.properties.HuaWeiKafkaProperties;
import com.sutpc.sutpcdatabusgpsgmkafka.utils.KafkaHuaweiConsumerUtils;
import java.io.File;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@Slf4j
public class SutpcDataBusGpsGmKafkaApplication implements CommandLineRunner {

  @Value("${kafka.huawei.consumer.topic}")
  private String topic;
  @Value("${spring.kafka.producer.topic}")
  private String sutpcTopic;
  @Autowired
  private HuaWeiKafkaProperties huaWeiKafkaProperties;

  private KafkaTemplate<String, String> kafkaTemplate;

  public static void main(String[] args) {
    SpringApplication.run(SutpcDataBusGpsGmKafkaApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    afterPropertiesSet();
    KafkaHuaweiConsumerUtils kafkaHuaweiConsumerUtils = new KafkaHuaweiConsumerUtils();
    kafkaHuaweiConsumerUtils.receive(integerStringConsumerRecord -> {
      log.info(integerStringConsumerRecord.value());
      kafkaTemplate.send(sutpcTopic, integerStringConsumerRecord.value());
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
