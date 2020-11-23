package com.sutpc.data.rev.expressway.microwave.rta.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TSeReportKey {

  /**
   * 日期.
   */
  private Date period;
  /**
   * 公路编码.
   */
  private String intersectioncode;
  /**
   * 日期类型： 1：日报 2：周报 3：月报 4：年报 5：1小时 6：15分钟.
   */
  private Long datetype;
}