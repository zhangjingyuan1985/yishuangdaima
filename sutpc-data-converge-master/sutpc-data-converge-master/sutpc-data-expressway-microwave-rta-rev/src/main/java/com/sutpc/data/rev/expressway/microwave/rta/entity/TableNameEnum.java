package com.sutpc.data.rev.expressway.microwave.rta.entity;

/**
 * topic名称枚举格.
 */
public enum TableNameEnum {
  /**
   * 地磁报表数据topic名称.
   */
  TDRIVEWAYDATA("sutpc-data-expressway-st-t-drivewaydata-original"),
  /**
   * 地磁出入口信息表topic名称.
   */
  TINTERSECTION("sutpc-data-expressway-st-t-intersection-original"),
  /**
   * 用户表topic名称.
   */
  TMANAGER("sutpc-data-expressway-highway-t-manager-original"),
  /**
   * 权限表topic名称.
   */
  TMANAGERROLE("sutpc-data-expressway-highway-t-manager-role-original"),
  /**
   * 微波道路信息表topic名称.
   */
  TMWLANEINFO("sutpc-data-expressway-highway-t-mw-lane-info-original"),
  /**
   * 微波设备信息表topic名称.
   */
  TMWREPORT("sutpc-data-expressway-highway-t-mw-report-original"),
  /**
   * 微波设备信息表topic名称.
   */
  TMWZTERMINALINFO("sutpc-data-expressway-highway-t-mw-terminal-info-original"),
  /**
   * 排队长度历史记录表topic名称.
   */
  TSEQUEUEHIS("sutpc-data-expressway-highway-t-se-queue-his-original"),
  /**
   * 地磁报表数据表topic名称.
   */
  TSEREPORT("sutpc-data-expressway-highway-t-se-report-original"),
  /**
   * 微波报表数据表topic名称.
   */
  TMWTERMINALDATASTA("sutpc-data-expressway-highway-t-mw-terminal-data-sta-original");
  // 成员变量
  private String name;

  TableNameEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "TableNameEnum{"
        + "name='" + name + '\''
        + '}';
  }

}
