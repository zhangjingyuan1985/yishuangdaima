package com.sutpc.data.rev.bus.gps.adsch.bean;

import lombok.Data;


@Data
public class GpsBean {

  /**
   * 终端Id.
   */
  private String terminalId;
  /**
   * 车辆Id.
   */
  private String vehicleId;
  /**
   * 线络Id.
   */
  private String lineId;
  /**
   * 子线络Id.
   */
  private String subLineId;
  /**
   * 组织名称编码.
   */
  private int nameCode;
  /**
   * 定位状态.
   */
  private int status;
  /**
   * 定位经度.
   */
  private float longitude;
  /**
   * 定位纬度.
   */
  private float latitude;
  /**
   * 定位高程.
   */
  private float elevation;

  /**
   * 定位时间.
   */
  private String time;
  /**
   * 定位速度.
   */
  private float speed;
  /**
   * 定位方向.
   */
  private float direction;
  /**
   * 行车记录仪速度.
   */
  private float recordSpeed;
  /**
   * 记录仪里程.
   */
  private float recordMileage;


}
