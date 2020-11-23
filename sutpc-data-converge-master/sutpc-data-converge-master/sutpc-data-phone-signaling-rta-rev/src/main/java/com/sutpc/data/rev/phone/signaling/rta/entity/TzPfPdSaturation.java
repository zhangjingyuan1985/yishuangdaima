package com.sutpc.data.rev.phone.signaling.rta.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 深圳市全市105个交通小区实时进出客流数据.
 */
@Data
public class TzPfPdSaturation {

  /**
   * 交通小区编号.
   */
  private Long traId;
  /**
   * 交通小区名称.
   */
  private String traName;

  /**
   * 无实际含义.
   */
  private Long zoom;
  /**
   * 时间戳.
   */
  private LocalDateTime timestamp;

  /**
   * 停留客流量.
   */
  private Long pfStay;

  /**
   * 客流密度.
   */
  private Long pd;

  /**
   * 进入客流量.
   */
  private Long pfIn;
  /**
   * 离开客流量.
   */
  private Long pfOut;

  @JSONField(name = "TRA_ID")
  public void setTraId(Long traId) {
    this.traId = traId;
  }

  @JSONField(name = "TRA_NAME")
  public void setTraName(String traName) {
    this.traName = traName;
  }

  @JSONField(name = "ZOOM")
  public void setZoom(Long zoom) {
    this.zoom = zoom;
  }

  @JSONField(name = "TIMESTAMP")
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @JSONField(name = "PF_STAY")
  public void setPfStay(Long pfStay) {
    this.pfStay = pfStay;
  }

  @JSONField(name = "PD")
  public void setPd(Long pd) {
    this.pd = pd;
  }

  @JSONField(name = "PF_IN")
  public void setPfIn(Long pfIn) {
    this.pfIn = pfIn;
  }

  @JSONField(name = "PF_OUT")
  public void setPfOut(Long pfOut) {
    this.pfOut = pfOut;
  }
}