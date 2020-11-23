package com.sutpc.data.rev.transport.rta.remote;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 远程接口调用.
 *
 * @author admin
 * @date 2020/8/4 14:48
 */
@Slf4j
@Component
public class RemoteHelper {

  @Autowired
  private RestTemplate restTemplate;
  @Value("${url.root.api}")
  private String apiRootUrl;
  @Value("${url.root.access-key}")
  private String accessKey;

  /**
   * post请求.
   *
   * @param path 请求路径
   * @return 响应数据
   */
  public RemoteResult post(String path, Map<String, Object> params) {
    params = params == null ? new HashMap<>() : params;
    params.put("accessKey", accessKey);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept-Charset", "utf-8");
    headers.set("Content-type", "application/json; charset=utf-8");
    HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(params), headers);
    String url = apiRootUrl + path;
    log.info("post url = {}", url);
    RemoteResult result = new RemoteResult();
    result.setState(false);
    try {
      String text = restTemplate.postForObject(url, entity, String.class);
      log.info("post text = {}", text);
      result = JSON.parseObject(text, RemoteResult.class);
    } catch (Exception e) {
      log.error("post error = {}", e);
      e.printStackTrace();
    }
    return result;
  }

}
