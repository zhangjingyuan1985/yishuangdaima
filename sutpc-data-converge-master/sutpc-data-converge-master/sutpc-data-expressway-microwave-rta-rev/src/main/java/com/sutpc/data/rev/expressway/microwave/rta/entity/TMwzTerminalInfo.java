package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

/**
 * 微波设备信息表.
 *
 * @author smilesnake.
 */
@Data
public class TMwzTerminalInfo {

  /**
   * 终端ID.
   */
  private Long terminalid;
  /**
   * 创建时间.
   */
  private Date createtime;
  /**
   * 描述.
   */
  private String description;

  private Long freespeed;

  private Long limitspeed;
  /**
   * 经纬度.
   */
  private String lonlat;

  private String showname;
  /**
   * 终端名称.
   */
  private String terminalname;
}