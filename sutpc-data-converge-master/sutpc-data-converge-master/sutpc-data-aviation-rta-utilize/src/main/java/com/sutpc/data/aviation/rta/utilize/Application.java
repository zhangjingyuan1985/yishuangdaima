package com.sutpc.data.aviation.rta.utilize;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@MapperScan("com.sutpc.data.aviation.rta.utilize.dao")
@EnableScheduling
@SpringBootApplication
public class Application {

  @Bean
  public RestTemplate reRestTemplate() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(20000);
    requestFactory.setReadTimeout(20000);
    RestTemplate restTemplate = new RestTemplate(requestFactory);

    List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
    HttpMessageConverter<?> converterTarget = null;
    for (HttpMessageConverter<?> item : converterList) {
      if (item.getClass() == StringHttpMessageConverter.class) {
        converterTarget = item;
        break;
      }
    }

    if (converterTarget != null) {
      converterList.remove(converterTarget);
    }
    HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
    converterList.add(converter);

    return restTemplate;
  }


  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
