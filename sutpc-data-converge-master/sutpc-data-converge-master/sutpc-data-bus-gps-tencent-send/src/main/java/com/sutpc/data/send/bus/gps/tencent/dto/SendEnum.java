package com.sutpc.data.send.bus.gps.tencent.dto;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/1 19:21
 */
public enum SendEnum {
  Kafka("sz_bus_from_kafka"),
  Tencent("sz_bus_to_tencent");

  String type;

  SendEnum(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
