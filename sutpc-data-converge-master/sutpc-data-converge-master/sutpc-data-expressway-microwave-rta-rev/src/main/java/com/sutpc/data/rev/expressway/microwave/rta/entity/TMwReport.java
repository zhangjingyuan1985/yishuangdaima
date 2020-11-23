package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 微波报表数据表.
 *
 * @author smilesnake
 */
@Data
public class TMwReport {

  /**
   * 数据类型.
   */
  private Long datetype;
  /**
   * 平均速度.
   */
  private Long avgspeed;
  /**
   * 方向.
   */
  private Long direction;
  /**
   * 车流量（辆）.
   */
  private Long flow;
  /**
   * PCU当量.
   */
  private Long pcu;
  /**
   * 数据时间.
   */
  private Date period;
  /**
   * 终端ID.
   */
  private Long terminalid;
  /**
   * 小型车平均车速.
   */
  private Long type1avgspeed;
  /**
   * 小型车PCU.
   */
  private Long type1pcu;
  /**
   * 中型车平均车速.
   */
  private Long type2avgspeed;
  /**
   * 中型车PCU.
   */
  private Long type2pcu;
  /**
   * 中型车平均车速.
   */
  private Long type3avgspeed;
  /**
   * 大型车PCU.
   */
  private Long type3pcu;
  /**
   * 超大型车平均车速.
   */
  private Long type4avgspeed;
  /**
   * 超大型车PCU.
   */
  private Long type4pcu;
}