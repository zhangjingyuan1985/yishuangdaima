package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 泊位.
 *
 * @author admin
 * @date 2020/6/22 14:19
 */
@Data
public class BerthRes {

  @JSONField(name = "AreaName")
  private String areaName;
  @JSONField(name = "ManufacturerName")
  private String manufacturerName;
  @JSONField(name = "PricingStrategyName")
  private String pricingStrategyName;
  @JSONField(name = "BerthAndBerthType")
  private String berthAndBerthType;
  @JSONField(name = "SetPosition")
  private String setPosition;
  @JSONField(name = "LineDirection")
  private String lineDirection;
  @JSONField(name = "ParkStatus")
  private String parkStatus;
  @JSONField(name = "BerthStatus")
  private String berthStatus;
  @JSONField(name = "CantonName")
  private String cantonName;
  @JSONField(name = "SectionName")
  private String sectionName;
  @JSONField(name = "BerthCode")
  private String berthCode;

  private PriceRes price;
}
