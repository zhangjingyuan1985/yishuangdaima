package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 价格.
 *
 * @author admin
 * @date 2020/6/22 14:23
 */
@Data
public class PriceItem {

  @JSONField(name = "IsAllow")
  private String isAllow;
  @JSONField(name = "ParkTime")
  private String parkTime;
  @JSONField(name = "DateType")
  private String dateType;

}
