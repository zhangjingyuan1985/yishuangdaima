package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 地磁数据原始表（第三方接口表）.
 *
 * @author smilesnake
 */
@Data
public class TDrivewaydata {
  //车流量
  private Long flowid;
  //累计车头时距  毫秒
  private Long accumulateheadinterval;
  //累计车间距  毫秒
  private Long accumulateinterval;
  //累计占有时间  毫秒
  private Long accumulateoccupancy;
  //累计排队长度  米
  private Long accumulatequeuelength;
  //累计排队时间  毫秒
  private Long accumulatequeuetime;
  //累计车长  米
  private Double accumulatevehiclelength;
  //累计速度  千米/小时
  private Double accumulatevelocity;
  //平均车头时距  毫秒
  private Long averageheadinterval;
  //平均车间距  毫秒
  private Long averageinterval;
  //平均占有时间  毫秒
  private Long averageoccupancy;
  //平均车长  米
  private Double averagevehiclelength;
  //平均速度  千米/小时
  private Double averagevelocity;
  //车道代码
  private String drivewaycode;
  //路口代码
  private String intersectioncode;
  //大车流量  辆
  private Integer largervehiclecount;
  //最大车头时距  毫秒
  private Long maxheadinterval;
  //最大车间距  毫秒
  private Long maxinterval;
  //最大占有时间  毫秒
  private Long maxoccupancy;
  //最大排队长度  米
  private Long maxqueuelength;
  //最大车长  米
  private Double maxvehiclelength;
  //最大速度  千米/小时
  private Double maxvelocity;
  //中车流量  辆
  private Integer midsizevehiclecount;
  //最小车头时距  毫秒
  private Long minheadinterval;
  //最小车间距  毫秒
  private Long mininterval;
  //微型车流量  辆
  private Integer minitypevehiclecount;
  //最小占有时间  毫秒
  private Long minoccupancy;
  //最小排队长度  米
  private Long minqueuelength;
  //最小车长  米
  private Double minvehiclelength;
  //最小速度  千米/小时
  private Double minvelocity;
  //摩托车流量  辆
  private Integer motovehiclecount;
  //前方车道满累计时间  毫秒
  private Long precedingwayfulltime;
  //预留6个车型  辆
  private Integer reservercount1;
  //
  private Integer reservercount2;
  //
  private Integer reservercount3;
  //
  private Integer reservercount4;
  //
  private Integer reservercount5;
  //
  private Integer reservercount6;
  //闯红灯次数
  private Long runredlightcount;
  //流水时间
  private Date sendtime;
  //小车流量  辆
  private Integer smallvehiclecount;
  //路口流水号  采集中心产生的流水号
  private Long subflowid;
  //启用标志
  private Integer vdtype;
  //车流量，为各种类型车辆流量的和
  private Integer vehiclecount;
}