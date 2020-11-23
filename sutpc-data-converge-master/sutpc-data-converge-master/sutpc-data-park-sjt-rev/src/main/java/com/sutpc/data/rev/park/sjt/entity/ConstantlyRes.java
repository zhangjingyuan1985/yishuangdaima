package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 剩余车位.
 *
 * @author admin
 * @date 2020/6/22 20:42
 */
@Data
public class ConstantlyRes {

  @JSONField(name = "LoadIndex")
  private String loadIndex;
  @JSONField(name = "UseCount")
  private String useCount;
  @JSONField(name = "AreaId")
  private String areaId;
  @JSONField(name = "CantonId")
  private String cantonId;
  @JSONField(name = "BerthVacant")
  private String berthVacant;
  @JSONField(name = "AreaName")
  private String areaName;
  @JSONField(name = "NotUsedCount")
  private String notUsedCount;
  @JSONField(name = "SectionLatitude")
  private String sectionLatitude;
  @JSONField(name = "BerthTotal")
  private String berthTotal;
  @JSONField(name = "TotalCounts")
  private String totalCounts;
  @JSONField(name = "CantonName")
  private String cantonName;
  @JSONField(name = "SectionName")
  private String sectionName;
  @JSONField(name = "SectionLongitude")
  private String sectionLongitude;
  @JSONField(name = "SectionId")
  private String sectionId;


}
