package com.sutpc.data.rev.expressway.microwave.rta.entity;

import lombok.Data;

/**
 * 地磁出入口信息表.
 *
 * @author smilesnake
 */
@Data
public class TIntersection {
  /**
   * 出入口编号.
   */
  private String intersectioncode;
  /**
   *所属区域.
   */
  private String childareacode;
  /**
   *高速公路ID.
   */
  private String highwayId;
  /**
   *站点名称.
   */
  private String stationName;
  /**
   *出入口名称.
   */
  private String intersectionname;
  /**
   *出入口类型.
   */
  private String intersectiontype;
  /**
   *监控ID.
   */
  private String monitorId;
  /**
   *站点ID.
   */
  private String stationId;
  /**
   *设计图.
   */
  private byte[] intersectionmap;
  /**
   * 高速公路名称.
   */
  private String highwayName;
}