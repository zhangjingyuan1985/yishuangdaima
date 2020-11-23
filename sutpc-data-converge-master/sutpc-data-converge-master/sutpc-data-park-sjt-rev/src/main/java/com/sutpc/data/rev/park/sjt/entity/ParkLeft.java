package com.sutpc.data.rev.park.sjt.entity;

import lombok.Data;

/**
 * 实时停车剩余信息.
 *
 * @author admin
 * @date 2020/6/23 8:57
 */
@Data
public class ParkLeft {

  private Integer date;
  private Integer timeDim = 3;
  private Integer period;
  private String fid;
  private String version = "2020M6_sz_#508";
  private Integer leftNum;
  private Float saturation;
  private String source = "宜停车";

}
