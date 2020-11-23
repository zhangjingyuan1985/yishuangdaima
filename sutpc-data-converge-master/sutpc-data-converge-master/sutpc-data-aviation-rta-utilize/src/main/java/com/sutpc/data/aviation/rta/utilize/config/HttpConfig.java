package com.sutpc.data.aviation.rta.utilize.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * http参数配置.
 * @Auth smilesnake minyikun
 * @Create 2019/12/27 17:21 
 */
@ConfigurationProperties(prefix = "http")
@Component
@Data
@Slf4j
public class HttpConfig {

  /**
   * api地址
   */
  private String address;

  /**
   *身份标识.
   */
  private String opCode;
  /**
   *授权密码.
   */
  private String opPsw;
  /**
   *鉴权密钥.
   */
  private String key;
  /**
   * 纬度.
   */
  private Double lat;
  /**
   * 经度.
   */
  private Double lon;

  /**
   * 得到签权.
   */
  public String getSignature(Long timestamp) {
    String str = opCode + opPsw + timestamp + key;
    return DigestUtils.md5DigestAsHex((str.getBytes())).toUpperCase();
  }
}
