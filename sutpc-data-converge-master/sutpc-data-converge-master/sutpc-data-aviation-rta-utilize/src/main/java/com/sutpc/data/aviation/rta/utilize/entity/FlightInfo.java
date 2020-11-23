package com.sutpc.data.aviation.rta.utilize.entity;

import lombok.Data;

/**
 * 航班信息实体.
 */
@Data
public class FlightInfo {

  /**
   *.
   */
  private String id;
  /**
   *航班号.
   */
  private String flightno;
  /**
   *起飞时间.
   */
  private String deptime;
  /**
   *到达时间.
   */
  private String arrtime;
  /**
   *起飞状态.
   */
  private String fstatus;
  /**
   *
   */
  private String remark;
  /**
   *是否删除（0，未删除）.
   */
  private String isdelete;
  /**
   *创建时间.
   */
  private String createtime;
  /**
   *更新时间.
   */
  private String updatetime;
  /**
   *起飞城市.
   */
  private String fromcity;
  /**
   *到达城市.
   */
  private String tocity;
  /**
   * 0-深圳出发，1-抵达深圳
   */
  private String ftype;
  /**
   *航班名称.
   */
  private String companyid;
}