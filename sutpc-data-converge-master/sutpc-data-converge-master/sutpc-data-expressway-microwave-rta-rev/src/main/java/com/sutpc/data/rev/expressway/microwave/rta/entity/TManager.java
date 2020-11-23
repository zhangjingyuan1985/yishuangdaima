package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 用户表.
 *
 * @author smilesnake
 */
@Data
public class TManager {

  /**
   * 用户ID.
   */
  private Long id;
  /**
   * 创建时间.
   */
  private Date createTime;
  /**
   * 有效期.
   */
  private Date loginTime;
  /**
   * 如果指定，则只允许使用该IP登陆.
   */
  private String loginIp;
  /**
   * .
   */
  private Date modifyTime;
  /**
   * 用户名.
   */
  private String name;
  /**
   * 昵称.
   */
  private String nickName;
  /**
   * 密码.
   */
  private String password;
  /**
   * 用户类型.
   */
  private Long type;
}