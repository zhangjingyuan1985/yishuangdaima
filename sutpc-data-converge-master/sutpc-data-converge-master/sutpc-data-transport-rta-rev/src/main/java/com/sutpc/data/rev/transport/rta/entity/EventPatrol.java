package com.sutpc.data.rev.transport.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 巡查表.
 *
 * @author admin
 * @date 2020/8/4 14:22
 */
@Data
public class EventPatrol {

  //巡查日期
  private Date date;
  //巡查人员
  private String inspector;
  //结束时间
  private Date finishTime;
  //开始时间
  private Date startTime;
  //巡查距离（KM）
  private Float distance;
  //行政区名称
  private String districtName;
  //城市编号
  private Integer city = 440300;
}
