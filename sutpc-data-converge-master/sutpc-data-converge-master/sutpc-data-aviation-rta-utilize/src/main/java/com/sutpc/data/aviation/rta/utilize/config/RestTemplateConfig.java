package com.sutpc.data.aviation.rta.utilize.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * restfulTemplate配置.
 * @Auth smilesnake minyikun
 * @Create 2020/1/8 9:57 
 */
@Configuration
public class RestTemplateConfig {

  public class MediaTypeSetting extends MappingJackson2HttpMessageConverter {

    /**
     * .
     */
    public MediaTypeSetting() {
      List<MediaType> mediaTypes = new ArrayList<>();
      mediaTypes.add(MediaType.TEXT_PLAIN);
      mediaTypes.add(MediaType.TEXT_HTML);
      setSupportedMediaTypes(mediaTypes);
    }
  }


  /**
   * .
   * @return
   */
  @Bean
  public RestTemplate reRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(15000);
    factory.setReadTimeout(5000);

    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().add(new MediaTypeSetting());

    return restTemplate;
  }
}
