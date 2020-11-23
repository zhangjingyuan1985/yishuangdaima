package com.sutpc.data.rev.park.sjt.app;

import com.sutpc.data.rev.park.sjt.config.AppConfig;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * APP帮助类.
 *
 * @author admin
 * @date 2020/6/22 11:10
 */
@Slf4j
@Component
public class AppHelper {

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private AppConfig appConfig;

  /**
   * Get请求.
   *
   * @param path 请求路径
   * @param params 参数
   * @return 响应数据
   */
  public String get(String path, Map<String, String> params) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept-Charset", "utf-8");
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    StringBuilder sb = new StringBuilder();
    sb.append(appConfig.getApiRootUrl());
    sb.append(path);
    params = appConfig.buildParams(params);
    if (params != null && params.size() != 0) {
      sb.append("?");
      int index = 0;
      for (String key : params.keySet()) {
        sb.append(index == 0 ? "" : "&");
        sb.append(key);
        sb.append("=");
        sb.append(params.getOrDefault(key, ""));
        index++;
      }
    }
    String url = sb.toString();
    log.info("url = {}", url);
    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response = restTemplate
        .exchange(url, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }

}
