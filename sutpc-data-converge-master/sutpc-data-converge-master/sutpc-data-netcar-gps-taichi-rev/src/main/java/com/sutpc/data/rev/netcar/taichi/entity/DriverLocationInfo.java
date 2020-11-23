package com.sutpc.data.rev.netcar.taichi.entity;

import lombok.Data;

/**
 * 驾驶员位置信息.
 * <p>只是单纯的用来转成String，不关注类型</p>
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/6 16:05
 */
@Data
public class DriverLocationInfo {
  /**
   * 平台标示.
   */
  private Object platformCode;
  /**
   * 网约车驾驶员证号.
   */
  private Object driCertNo;
  /**
   * 驾档编号.
   */
  private Object licenseId;
  /**
   * 车牌号.
   */
  private Object vehicleNo;
  /**
   * 定位时间.
   */
  private Object positionTime;
  /**
   * 经度.
   */
  private Object longitude;
  /**
   * 纬度.
   */
  private Object latitude;
  /**
   * 坐标加密标识.
   */
  private Object encrypt;
  /**
   * 方向角.
   */
  private Object direction;
  /**
   * 海拔.
   */
  private Object elevation;
  /**
   * 瞬时速度.
   */
  private Object speed;
  /**
   * 运营状态.
   */
  private Object bizStatus;
  /**
   * 位置信息类型.
   */
  private Object positionType;
  /**
   * 订单号.
   */
  private Object orderId;
}