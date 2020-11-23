package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 深圳监管平台行政区.
 *
 * @author admin
 * @date 2020/6/22 14:13
 */
@Data
public class CantonRes {

  @JSONField(name = "CantonId")
  private String cantonId;
  @JSONField(name = "CantonName")
  private String cantonName;

}
