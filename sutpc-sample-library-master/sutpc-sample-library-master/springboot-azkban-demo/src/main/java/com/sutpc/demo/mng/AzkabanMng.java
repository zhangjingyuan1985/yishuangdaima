package com.sutpc.demo.mng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/22 10:05
 * @Modified By:
 */
@Component
public class AzkabanMng {
  private static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=utf-8";
  private static final String X_REQUESTED_WITH = "XMLHttpRequest";
  @Autowired
  private RestTemplate restTemplate;
  
  @Value("${azkaban.url:}")
  private String url;
  @Value("${azkaban.username:}")
  private String username;
  @Value("${azkaban.password:}")
  private String password;
  
  
}
