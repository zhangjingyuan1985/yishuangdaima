package com.sutpc.sutpc.data.ic.card.szt.kafka.initkey;

import com.sutpc.sutpc.data.ic.card.szt.kafka.properties.HuaWeiKafkaProperties;
import java.io.File;
import java.nio.file.Files;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 10:15 2020/6/22.
 * @Modified By:
 */
@Component
public class KeyFileInitHandler implements InitializingBean {

  @Autowired
  private HuaWeiKafkaProperties huaWeiKafkaProperties;

  @Override
  public void afterPropertiesSet() throws Exception {
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
