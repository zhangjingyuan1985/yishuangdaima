package com.sutpc.data.rev.transport.rta.res;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import lombok.Data;

/**
 * 巡查表.
 *
 * @author admin
 * @date 2020/8/4 14:22
 */
@Data
public class EventPatrolRes {

  //巡查日期
  @JSONField(name = "XunChaRiQi")
  private Date date;
  //巡查人员
  @JSONField(name = "XunChaRenYuan")
  private String inspector;
  //结束时间
  @JSONField(name = "JieShuShiJian")
  private Date finishTime;
  //开始时间
  @JSONField(name = "KaiShiShiJian")
  private Date startTime;
  //巡查距离（KM）
  @JSONField(name = "XunChaJuLi")
  private Float distance;
  //行政区名称
  @JSONField(name = "GuanLiXiaQu")
  private String districtName;
}
