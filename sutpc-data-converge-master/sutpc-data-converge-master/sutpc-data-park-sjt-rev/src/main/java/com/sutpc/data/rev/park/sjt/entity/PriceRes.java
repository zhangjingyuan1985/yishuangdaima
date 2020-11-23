package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import lombok.Data;

/**
 * 价格.
 *
 * @author admin
 * @date 2020/6/22 14:23
 */
@Data
public class PriceRes {

  @JSONField(name = "PricingStrategyName")
  private String pricingStrategyName;
  @JSONField(name = "WorkPricing")
  private String workPricing;
  @JSONField(name = "NoWorkPricing")
  private String noWorkPricing;
  private List<PriceItem> items;

}
