package com.sutpc.data.send.bus.gps.tencent.vo;

import com.sutpc.data.send.bus.gps.tencent.dto.InfoUnit;

import java.util.List;

public class Request {
  private int header;
  private int length;
  private List<InfoUnit> infoUnit;
  private int end;

  public Request() {
  }

  /**
   *  .
   */
  public Request(byte header, byte length, List<InfoUnit> infoUnit, int end) {
    this.header = header;
    this.length = length;
    this.infoUnit = infoUnit;
    this.end = end;
  }

  public int getHeader() {
    return header;
  }

  public void setHeader(int header) {
    this.header = header;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public List<InfoUnit> getInfoUnit() {
    return infoUnit;
  }

  public void setInfoUnit(List<InfoUnit> infoUnit) {
    this.infoUnit = infoUnit;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }
}
