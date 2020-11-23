package com.sutpc.sutpcdatabusgpsgmkafka.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 11:16 2020/6/22.
 * @Modified By:
 */
@Data
@Component
public class HuaWeiKafkaProperties {

  @Value("${kafka.huawei.user.keytab.path}")
  private String keytabPath;
  @Value("${kafka.huawei.security.krb5.config.path}")
  private String configPath;

}
