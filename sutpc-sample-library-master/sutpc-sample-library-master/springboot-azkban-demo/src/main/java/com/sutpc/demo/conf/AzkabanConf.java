package com.sutpc.demo.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/22 10:01
 * @Modified By:
 */
@Configuration
public class AzkabanConf {
  @Bean
  public RestTemplate getRestTemplate(){
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(2000);
    requestFactory.setReadTimeout(2000);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    return restTemplate;
  }
}
