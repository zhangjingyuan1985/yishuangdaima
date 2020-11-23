package com.sutpc.data.rev.transport.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 案件基本信息.
 *
 * @author admin
 * @date 2020/8/4 14:08
 */
@Data
public class EventInfo {

  //事件ID
  private String eventId;
  //事件类别
  private String category;
  //来源类别
  private String source;
  //报障人
  private String reportName;
  //报障单位
  private String reportCompany;
  //报障时间
  private Date reportTime;
  //业务类型
  private String businessType;
  //业务子类型
  private String businessSubtype;
  //行政区
  private String districtName;
  //街道
  private String blockName;
  //故障路名
  private String faultName;
  //中路段编号
  private Integer roadSectFid;
  //故障类型
  private String faultType;
  //故障点经度
  private Float lng;
  //故障点纬度
  private Float lat;
  //案件编号
  private String caseFid;
  //事件描述
  private String description;
  //发生地点
  private String address;
  //上报类型
  private String reportType;
  //损坏程度
  private String damageExtent;
  //申报方式
  private String declareWay;
  //城市编号
  private Integer city = 440300;

}
