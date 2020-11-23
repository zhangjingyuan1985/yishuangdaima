package com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty;

public enum GpsTypeEnum {
  COACHBUS("tpCoachBus", 1), TAXI("tpTaxi", 2), FREIGHT("tpFreight", 4), DRIVING("tpDriving",
      8), CHARTERBUS("tpCharterBus", 16), TRANSIT("tpTransit",
      32), DANGEROUS("tpDangerous", 64), DUMPER("tpDumper", 256), OTHERS("tpOthers", 128);
  // 成员变量
  private String name;
  private int index;

  // 构造方法
  private GpsTypeEnum(String name, int index) {
    this.name = name;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  // 覆盖方法
  @Override
  public String toString() {
    return this.index + "_" + this.name;
  }

  /**
   *  .
   * @param value enum对应的int
   * @return
   */
  public static GpsTypeEnum getByValue(int value) {
    for (GpsTypeEnum code : values()) {
      if (code.getIndex() == value) {
        return code;
      }
    }
    return null;
  }

}