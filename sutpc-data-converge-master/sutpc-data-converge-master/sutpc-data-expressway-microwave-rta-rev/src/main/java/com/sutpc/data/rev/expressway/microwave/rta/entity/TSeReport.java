package com.sutpc.data.rev.expressway.microwave.rta.entity;

import lombok.Data;

/**
 * 地磁报表数据表.
 *
 * @author smilesnake
 */
@Data
public class TSeReport extends TSeReportKey {
  /**
   * 平均速度.
   */
  private Double avgspeed;
  /**
   * 车流量（量）.
   */
  private Double flow;
  /**
   * PCU当量.
   */
  private Double pcu;
}