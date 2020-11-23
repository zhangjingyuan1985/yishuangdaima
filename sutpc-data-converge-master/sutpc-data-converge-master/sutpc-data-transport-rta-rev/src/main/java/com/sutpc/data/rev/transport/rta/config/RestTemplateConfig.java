package com.sutpc.data.rev.transport.rta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * com.sutpc.accident.configuration.
 *
 * @author luoj
 * @date 2019/5/23 15:48
 */

@Configuration
public class RestTemplateConfig {

  /**
   * RestTemplate.
   */
  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
    return new RestTemplate(factory);
  }

  /**
   * 注入HTTP请求.
   */
  @Bean
  public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(150000);
    factory.setReadTimeout(50000);
    return factory;
  }
}
