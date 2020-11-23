package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 微波道路信息表.
 *
 * @author smilesnake
 */
@Data
public class TMwLaneInfo {
  /**
   * 所属终端.
   */
  private Long terminalid;
  /**
   * 创建时间.
   */
  private Date createtime;
  /**
   * 描述.
   */
  private String description;
  /**
   * 道路方向，1：动画显示从左到右，-1动画显示从右到左.
   */
  private Long direction;
  /**
   * 道路编号.
   */
  private Long lane;
  /**
   * 所属终端名称.
   */
  private String terminalname;
}