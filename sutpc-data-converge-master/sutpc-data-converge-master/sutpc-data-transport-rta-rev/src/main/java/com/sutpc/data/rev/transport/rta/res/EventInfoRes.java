package com.sutpc.data.rev.transport.rta.res;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import lombok.Data;

/**
 * 案件基本信息返回数据.
 *
 * @author admin
 * @date 2020/8/4 14:40
 */
@Data
public class EventInfoRes {

  //事件ID
  @JSONField(name = "EventID")
  private String eventId;
  //事件类别
  @JSONField(name = "ShiJianLeiBie")
  private String category;
  //来源类别
  @JSONField(name = "LaiYuanLeiBie")
  private String source;
  //报障人
  @JSONField(name = "BaoZhangRen")
  private String reportName;
  //报障单位
  @JSONField(name = "BaoZhangDanWei")
  private String reportCompany;
  //报障时间
  @JSONField(name = "BaoZhangShiJian")
  private Date reportTime;
  //业务类型
  @JSONField(name = "YeWuLeiXing")
  private String businessType;
  //业务子类型
  @JSONField(name = "YeWuZiLeiXing")
  private String businessSubtype;
  //行政区
  @JSONField(name = "YiJiQuYu")
  private String districtName;
  //街道
  @JSONField(name = "ErJiQuYu")
  private String blockName;
  //故障路名
  @JSONField(name = "GuZhangLuMing")
  private String faultName;
  //故障类型
  @JSONField(name = "GuZhangLeiXing")
  private String faultType;
  //故障点经度
  @JSONField(name = "GuZhangDianJingDu")
  private Float lng;
  //故障点纬度
  @JSONField(name = "GuZhangDianWeiDu")
  private Float lat;
  //案件编号
  @JSONField(name = "AnJianBianHao")
  private String caseFid;
  //事件描述
  @JSONField(name = "ShiJianMiaoShu")
  private String description;
  //发生地点
  @JSONField(name = "FaShengDiDian")
  private String address;
  //上报类型
  @JSONField(name = "ShangBaoLeiXing")
  private String reportType;
  //损坏程度
  @JSONField(name = "SunHuaiChengDu")
  private String damageExtent;
  //申报方式
  @JSONField(name = "ShenBaoFangShi")
  private String declareWay;

}
