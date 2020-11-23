package com.sutpc.data.rev.phone.signaling.rta.entity;

/**
 * topic名称枚举格.
 */
public enum TableNameEnum {
  /**
   * 深圳指挥中心实时热点区域客流数据.
   */
  TBL_PF_SATURATION("sutpc-data-phone-signaling-mdpfws-biz-tbl-pf-saturation-original");
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
