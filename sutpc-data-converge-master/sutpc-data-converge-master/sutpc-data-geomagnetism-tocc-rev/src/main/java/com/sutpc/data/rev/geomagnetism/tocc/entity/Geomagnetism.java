package com.sutpc.data.rev.geomagnetism.tocc.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import lombok.Data;

/**
 * 地磁信息.
 *
 * @author admin
 * @date 2020/6/17 9:34
 */
@Data
public class Geomagnetism {

  @JSONField(name = "intersectioncode")
  private String code;
  @JSONField(name = "intersectionname")
  private String name;
  @JSONField(name = "sequence")
  private List<String> sequences;
  @JSONField(name = "lanes")
  private Integer laneCount;
  @JSONField(name = "road_type")
  private Integer roadType;
  @JSONField(name = "map_lat")
  private Double lat;
  @JSONField(name = "map_lon")
  private Double lng;

}
