package com.sutpc.data.rev.transport.rta.res;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import lombok.Data;

/**
 * 办理信息.
 *
 * @author admin
 * @date 2020/8/4 14:19
 */
@Data
public class EventStatusRes {

  //事件ID
  @JSONField(name = "EventID")
  private String eventFid;
  //状态
  @JSONField(name = "ZhuangTai")
  private String status;
  //进入当前环节时间
  @JSONField(name = "JinRuHuanJieShiJian")
  private Date updateTime;
  //办结类型
  @JSONField(name = "BanJieLeiXing")
  private String finishType;
  //办结时间
  @JSONField(name = "BanJieShiJian")
  private Date finishTime;
  //办结方式
  @JSONField(name = "BanJieFangShi")
  private String finishWay;
  //办结说明
  @JSONField(name = "BanJieShuoMing")
  private String finishRemark;

}
