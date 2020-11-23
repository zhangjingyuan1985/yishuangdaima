package com.sutpc.data.aviation.rta.utilize.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  航班动态接口返回消息实体.
 * @Auth smilesnake minyikun
 * @Create 2019/12/27 16:18 
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlainDynamicDto {

  /**
   *航班号.
   */
  private String fltno;
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
  private String flightState;
  /**
   *起飞城市.
   */
  private String fromCity;
  /**
   *到达城市.
   */
  private String endCity;
  /**
   *.
   */
  private String flyId;
  /**
   *航空公司的中文名称.
   */
  private String airLineName;
}
