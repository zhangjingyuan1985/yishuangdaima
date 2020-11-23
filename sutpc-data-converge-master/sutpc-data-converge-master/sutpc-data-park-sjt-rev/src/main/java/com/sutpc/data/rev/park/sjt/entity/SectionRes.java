package com.sutpc.data.rev.park.sjt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 路段.
 *
 * @author admin
 * @date 2020/6/22 14:16
 */
@Data
public class SectionRes {

  @JSONField(name = "SectionId")
  private String sectionId;
  @JSONField(name = "SectionName")
  private String sectionName;
  @JSONField(name = "SectionLatitude")
  private String sectionLatitude;
  @JSONField(name = "SectionLongitude")
  private String sectionLongitude;

  private String cantonName;

}
