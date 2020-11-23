package com.sutpc.data.rev.park.sjt.config;

import cn.hutool.crypto.SecureUtil;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * App配置类.
 *
 * @author admin
 * @date 2020/6/22 10:40
 */
@Component
public class AppConfig {

  @Value("${app.sjt.op-code}")
  private String code;
  @Value("${app.sjt.op-pws}")
  private String password;
  @Value("${app.sjt.key}")
  private String key;
  @Value("${app.sjt.api-root-url}")
  private String apiRootUrl;

  /**
   * 获取当前东八区时间戳.
   *
   * @return 秒数
   */
  private long currentTimeStamp() {
    return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
  }

  /**
   * 获取签名.
   *
   * @param timestamp 时间戳
   * @return 签名
   */
  private String signature(long timestamp) {
    String text = String.format("%s%s%d%s", code, password, timestamp, key);
    String signature = SecureUtil.md5(text);
    return signature.toUpperCase();
  }

  /**
   * 构建查询参数.
   *
   * @param params 非用户标识范围内的参数，可为空
   * @return 参数列表
   */
  public Map<String, String> buildParams(Map<String, String> params) {
    Map<String, String> results = new HashMap<>();
    results.put("opCode", code);
    long timestamp = currentTimeStamp();
    results.put("timeStamp", String.valueOf(timestamp));
    String signature = signature(timestamp);
    results.put("signature", signature);
    if (params != null) {
      results.putAll(params);
    }
    return results;
  }

  /**
   * 请求接口根地址.
   *
   * @return 地址
   */
  public String getApiRootUrl() {
    return apiRootUrl;
  }
}
