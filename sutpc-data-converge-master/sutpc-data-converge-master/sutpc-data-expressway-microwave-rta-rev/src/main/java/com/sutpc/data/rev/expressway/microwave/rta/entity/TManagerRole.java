package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 权限表.
 *
 * @author smilesnake
 */
@Data
public class TManagerRole {

  /**
   * 权限ID.
   */
  private Long id;
  /**
   * 创建时间.
   */
  private Date createTime;
  /**
   * 管理员ID.
   */
  private Long managerId;
  /**
   * 管理员角色ID.
   */
  private Long roleId;
}