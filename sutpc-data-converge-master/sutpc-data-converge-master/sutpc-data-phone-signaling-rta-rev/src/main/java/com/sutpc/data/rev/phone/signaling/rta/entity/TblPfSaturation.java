package com.sutpc.data.rev.phone.signaling.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 深圳指挥中心实时热点区域客流数据.
 */
@Data
public class TblPfSaturation {

  /**
   * 热点区域编号.
   */
  private Long tazid;
  /**
   * 时间戳，更新周期（15min）.
   */
  private Date timestamp;
  /**
   * 热点区域名称.
   */
  private String tazname;
  /**
   * 客流值（单位：人）.
   */
  private Long pf;
  /**
   * 客流饱和度值.
   */
  private Long pfSaturation;
  /**
   * 服务水平.
   */
  private String serviceLevel;


}