package com.sutpc.data.rev.transport.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 办理信息.
 *
 * @author admin
 * @date 2020/8/4 14:19
 */
@Data
public class EventStatus {

  //事件ID
  private String eventFid;
  //状态
  private String status;
  //进入当前环节时间
  private Date updateTime;
  //办结类型
  private String finishType;
  //办结时间
  private Date finishTime;
  //办结方式
  private String finishWay;
  //办结说明
  private String finishRemark;
  //城市编号
  private Integer city = 440300;

}
