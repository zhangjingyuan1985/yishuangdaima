package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 排队长度历史记录表.
 *
 * @author smilesnake
 */
@Data
public class TSeQueueHis {

  /**
   * 公路编号.
   */
  private String intersectioncode;
  /**
   * 开放闸口数量.
   */
  private Long opengatenum;
  /**
   * 排队长度.
   */
  private Double queuelength;
  /**
   * 记录时间.
   */
  private Date recordtime;
}