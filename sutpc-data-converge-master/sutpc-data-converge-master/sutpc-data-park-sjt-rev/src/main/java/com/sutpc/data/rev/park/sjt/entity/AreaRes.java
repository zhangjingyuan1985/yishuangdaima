package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 片区.
 *
 * @author admin
 * @date 2020/6/22 14:15
 */
@Data
public class AreaRes {

  @JSONField(name = "AreaId")
  private String areaId;
  @JSONField(name = "AreaName")
  private String areaName;

}
