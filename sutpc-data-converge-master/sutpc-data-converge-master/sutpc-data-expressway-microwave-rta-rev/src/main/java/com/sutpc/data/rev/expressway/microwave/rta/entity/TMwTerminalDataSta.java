package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 微波报表数据表.
 */
@Data
public class TMwTerminalDataSta {

  /**
   * 终端ID.
   */
  private Long terminalid;
  /**
   * 采集时间，数据中的时间.
   */
  private Date collectiontime;
  /**
   * 创建时间，写入数据库的时间.
   */
  private Date createtime;
  /**
   * 车道编号.
   */
  private Integer lane;
  /**
   * 通过的车辆数.
   */
  private Integer vehiclenum;
  /**
   * 时间占有率.
   */
  private Double occ;
  /**
   * 平均速度.
   */
  private Integer speedAvg;
  /**
   * 平均车间时距.
   */
  private Integer gapAvg;
  /**
   * 车型1数量(小车).
   */
  private Integer numType1;
  /**
   * 车型2数量(中车).
   */
  private Integer numType2;
  /**
   * 车型3数量(大车).
   */
  private Integer numType3;
  /**
   * 车型4数量(超大车).
   */
  private Integer numType4;
  /**
   * 车型5数量(其他，备用).
   */
  private Integer numType5;
}